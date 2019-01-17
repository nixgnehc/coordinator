package cn.bbqiu.middleware.zk;

import cn.bbqiu.middleware.AbstractCoordinator;
import cn.bbqiu.middleware.CoordinatorTaskLoading;
import cn.bbqiu.middleware.ReBalanceSource;
import cn.bbqiu.middleware.TaskManager;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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


    private void initTask() {
        try {
            local.setCoordinatorTask(zkLocal.getClient().getChildren().forPath(zkDefine.getTaskBasePath()));
            local.setLocaTask(new ArrayList<>());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refreshTask() {

        InterProcessSemaphoreMutex zkLock = new InterProcessSemaphoreMutex(zkLocal.getClient(), zkDefine.getLockBasePath());
        scheduled.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {

                    if (!(zkLock.acquire(0, TimeUnit.SECONDS) || zkLock.isAcquiredInThisProcess())) {
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
    public void doTaskRefresh() {
        try {
            logger.debug(String.format("listenter:%s", zkDefine.getTaskBasePath()));
            PathChildrenCache cache = new PathChildrenCache(zkLocal.getClient(), zkDefine.getTaskBasePath(), true);

            cache.getListenable().addListener(new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                    logger.debug("监听到任务变化事件");
                    balance(ReBalanceSource.TASK);
                }

            });
            cache.start();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("localTask:", e);
        }
    }


    @Override
    public void doPeersRefresh() {

        try {
            PathChildrenCache cache = new PathChildrenCache(zkLocal.getClient(), zkDefine.getPeersBasePath(), true);

            cache.getListenable().addListener(new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                    logger.debug("监听到一个节点变化事件");
                    balance(ReBalanceSource.PEER);
                }
            });
            cache.start();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("peers:", e);
        }
    }

    @Override
    public void registerLocalPeer(){
        zkBiz.createProvisionalPath(String.format("%s/%s", zkDefine.getPeersBasePath(), peerName));
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
    public void doLocalRefresh() {
        try {
            local.setPeerNum(zkLocal.getClient().getChildren().forPath(zkLocal.getZkDefine().getPeersBasePath()).size());
            local.setCoordinatorTask(zkLocal.getClient().getChildren().forPath(zkLocal.getZkDefine().getTaskBasePath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
