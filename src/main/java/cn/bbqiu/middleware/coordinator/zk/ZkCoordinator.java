package cn.bbqiu.middleware.coordinator.zk;

import cn.bbqiu.middleware.coordinator.AbstractCoordinator;
import cn.bbqiu.middleware.coordinator.TaskManager;
import com.google.common.collect.Lists;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
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

    private TaskManager taskManager;

    private int taskRefreshFrequency;

    private Logger logger = LoggerFactory.getLogger(ZkCoordinator.class);

    /**
     * @param zkHosts  host:port,host1:port1......
     * @param basePath zk中的基础目录
     */
    public ZkCoordinator(String zkHosts, String basePath) {
        this(zkHosts, basePath, 60);
    }

    public ZkCoordinator(String zkHosts, String basePath, int taskRefreshFrequency) {
        this.taskRefreshFrequency = taskRefreshFrequency;
        zkLocal = new ZkLocal();
        zkLocal.setZkDefine(new ZkDefine(zkHosts, basePath));
        local = zkLocal;
    }

    @Override
    public void refreshTask() {

        InterProcessSemaphoreMutex zkLock = new InterProcessSemaphoreMutex(zkLocal.getClient(), zkLocal.getZkDefine().getLockBasePath());
        scheduled.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {

                    if (!(zkLock.acquire(0, TimeUnit.SECONDS) || zkLock.isAcquiredInThisProcess())) {
                        return;
                    }
                    List<String> list = new ArrayList<>(taskLoad.refresh());
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
            logger.debug(String.format("listenter:%s", zkLocal.getZkDefine().getTaskBasePath()));
            PathChildrenCache cache = new PathChildrenCache(zkLocal.getClient(), zkLocal.getZkDefine().getTaskBasePath(), true);

            cache.getListenable().addListener(new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                    logger.debug("监听到任务变化");
                    balance();
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
            PathChildrenCache cache = new PathChildrenCache(zkLocal.getClient(), zkLocal.getZkDefine().getPeersBasePath(), true);

            cache.getListenable().addListener(new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                    logger.debug("监听到一个节点变化事件");
                    balance();
                }
            });
            cache.start();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("peers:", e);
        }
    }

    @Override
    public void registerLocalPeer() {
        zkBiz.createProvisionalPath(String.format("%s/%s", zkLocal.getZkDefine().getPeersBasePath(), nodeName));
    }

    @Override
    public void init() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

        zkLocal.setZkDefine(zkLocal.getZkDefine());

        zkLocal.setClient(CuratorFrameworkFactory.newClient(zkLocal.getZkDefine().getZkHosts(), retryPolicy));
        zkLocal.getClient().start();

        zkBiz = new ZkBiz(zkLocal.getClient());


        this.initZkBasePath();

        taskManager = new ZkTaskManager(zkBiz, zkLocal.getZkDefine());

        reBalance = new ZkReBalance(zkLocal);
    }

    public void initZkBasePath() {

        zkBiz.createPath(zkLocal.getZkDefine().getBasePath());
        zkBiz.createPath(zkLocal.getZkDefine().getTaskBasePath());
        zkBiz.createPath(zkLocal.getZkDefine().getPeersBasePath());

    }


    @Override
    public void doLocalRefresh() {
        try {
            if (null == local.getLocaTask()) {
                local.setLocaTask(Lists.newArrayList());
            }
            local.setPeerNum(zkLocal.getClient().getChildren().forPath(zkLocal.getZkDefine().getPeersBasePath()).size());
            local.setCoordinatorTask(zkLocal.getClient().getChildren().forPath(zkLocal.getZkDefine().getTaskBasePath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
