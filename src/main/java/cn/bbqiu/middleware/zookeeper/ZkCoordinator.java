package cn.bbqiu.middleware.zookeeper;

import cn.bbqiu.middleware.AbstractCoordinator;
import cn.bbqiu.middleware.ReBalanceSource;
import cn.bbqiu.middleware.TaskManager;
import cn.bbqiu.middleware.utils.LocalUtil;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
* @author: nixgnehc nixgnehc@163.com
 * @date: 19-1-15
 * @time: 下午10:36
 * @Description: TODO..
 */

public class ZkCoordinator extends AbstractCoordinator {

    private ZkBiz zkBiz;

    private ZkLocal zkLocal;

    private ZkDefine zkDefine;

    private TaskManager taskManager;

    /**
     * @param zkHosts  host:port,host1:port1......
     * @param basePath zk中的基础目录
     */
    public ZkCoordinator(String zkHosts, String basePath) {
        zkDefine = new ZkDefine(zkHosts, basePath);
    }


    @Override
    public void coordinatorTask() {

        InterProcessSemaphoreMutex zkLock = new InterProcessSemaphoreMutex(zkLocal.getClient(), zkDefine.getLockBasePath());
        scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    if (zkLock.acquire(0, TimeUnit.SECONDS) || zkLock.isAcquiredInThisProcess()) {
                        List<String> list = taskLoad.loading();
                        taskManager.revise(list, zkLocal.getCoordinatorTask());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 10, 60, TimeUnit.SECONDS);
    }

    @Override
    public void localTask() {
        new NodeCache(zkLocal.getClient(), zkDefine.getTaskBasePath()).getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                zkLocal.setLocaTask(zkLocal.getClient().getChildren().forPath(zkDefine.getTaskBasePath()));
                balance(ReBalanceSource.TaskChange);
            }
        });
    }

    @Override
    public void peers() {

        new NodeCache(zkLocal.getClient(), zkDefine.getPeersBasePath()).getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                zkLocal.setPeerNum(zkLocal.getClient().getChildren().forPath(zkDefine.getPeersBasePath()).size());
                balance(ReBalanceSource.PeerChange);
            }
        });

        zkBiz.createProvisionalPath(String.format("%s/%s", zkDefine.getPeersBasePath(), LocalUtil.sign()));
    }

    @Override
    public void init() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        local = new ZkLocal();
        zkLocal = (ZkLocal) local;

        zkLocal.setClient(CuratorFrameworkFactory.newClient(zkDefine.getZkHosts(), retryPolicy));
        zkLocal.getClient().start();

        zkBiz = new ZkBiz(zkLocal.getClient());
        zkBiz.createPath(zkDefine.getBasePath());
        zkBiz.createPath(zkDefine.getTaskBasePath());

        taskManager = new ZkTaskManager(zkBiz, zkDefine);

        reBalance = new ZkReBalance(zkLocal);
    }


}
