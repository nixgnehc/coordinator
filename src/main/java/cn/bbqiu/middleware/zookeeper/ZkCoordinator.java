package cn.bbqiu.middleware.zookeeper;

import cn.bbqiu.middleware.AbstractCoordinator;
import cn.bbqiu.middleware.ReBalanceSource;
import cn.bbqiu.middleware.TaskManager;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.soap.Node;
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

    private int taskRefreshFrequency = 60;

    private Logger logger = LoggerFactory.getLogger(ZkCoordinator.class);

    /**
     * @param zkHosts  host:port,host1:port1......
     * @param basePath zk中的基础目录
     */
    public ZkCoordinator(String zkHosts, String basePath) {
        zkDefine = new ZkDefine(zkHosts, basePath);
    }

    public ZkCoordinator(String zkHosts, String basePath, int taskRefreshFrequency) {
        this.taskRefreshFrequency = taskRefreshFrequency;
        zkDefine = new ZkDefine(zkHosts, basePath);
    }


    @Override
    public void coordinatorTask() {

        InterProcessSemaphoreMutex zkLock = new InterProcessSemaphoreMutex(zkLocal.getClient(), zkDefine.getLockBasePath());
        scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!(zkLock.acquire(0, TimeUnit.SECONDS) || zkLock.isAcquiredInThisProcess())) {
                        return;
                    }
                    if (maintenanceSign) {
                        taskManager.clean(local.getCoordinatorTask());
                        return;
                    }
                    List<String> list = taskLoad.loading();
                    taskManager.revise(list, zkLocal.getCoordinatorTask());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 10, taskRefreshFrequency, TimeUnit.SECONDS);
    }

    @Override
    public void localTask() {
        try {
            NodeCache nodeCache = new NodeCache(zkLocal.getClient(), zkDefine.getTaskBasePath());

            nodeCache.getListenable().addListener(new NodeCacheListener() {
                @Override
                public void nodeChanged() throws Exception {

                    List<String> list = zkLocal.getClient().getChildren().forPath(zkDefine.getTaskBasePath());
                    logger.debug(String.format("trigger localTask, task size:%d", list.size()));
                    zkLocal.setCoordinatorTask(list);
                    balance(ReBalanceSource.TASK);
                }
            });
            nodeCache.start(true);
        } catch (Exception e) {
            logger.error("localTask :", e);
            e.printStackTrace();
        }
    }

    @Override
    public void peers() {

        try {
            NodeCache nodeCache = new NodeCache(zkLocal.getClient(), zkDefine.getPeersBasePath());
            nodeCache.getListenable().addListener(new NodeCacheListener() {
                @Override
                public void nodeChanged() throws Exception {
                    zkLocal.setPeerNum(zkLocal.getClient().getChildren().forPath(zkDefine.getPeersBasePath()).size());
                    balance(ReBalanceSource.PEER);
                }
            });
            nodeCache.start(true);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("peers:", e);
        }

        zkBiz.createProvisionalPath(String.format("%s/%s", zkDefine.getPeersBasePath(), peerSign));
    }

    @Override
    public void init() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

        local = new ZkLocal();
        zkLocal = (ZkLocal) local;

        zkLocal.setClient(CuratorFrameworkFactory.newClient(zkDefine.getZkHosts(), retryPolicy));
        zkLocal.getClient().start();

        zkBiz = new ZkBiz(zkLocal.getClient());


        this.initZkBasePath();

        taskManager = new ZkTaskManager(zkBiz, zkDefine);

        reBalance = new ZkReBalance(zkLocal);
    }

    public void initZkBasePath() {

        zkBiz.createPath(zkDefine.getBasePath());
        zkBiz.createPath(zkDefine.getTaskBasePath());
        zkBiz.createPath(zkDefine.getPeersBasePath());

    }

    @Override
    public void maintenance() {
        zkBiz.createPath(zkDefine.getMaintenanceBasePath());
        taskManager.clean(zkLocal.getCoordinatorTask());
    }

    @Override
    public void atwork() {
        zkBiz.deletePath(zkDefine.getMaintenanceBasePath());
    }
}
