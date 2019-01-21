package cn.bbqiu.middleware.coordinator.zk;

import cn.bbqiu.middleware.coordinator.AbstractTask;
import com.google.common.collect.Lists;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: chengxin chengxin@zbj.com
 * @date: 19-1-21
 * @time: 下午11:16
 * @Description: TODO..
 */

public class ZkBiz {

    private CuratorFramework client;

    private String basePath;

    private Set<String> nodes;

    public ZkBiz(String hosts, String basePath, String local) throws Exception {
        this.basePath = basePath;
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.newClient(hosts, retryPolicy);
        client.start();
        createPath(basePath);
        createProvisionalPath(String.format("%s/%s", basePath, local));

        PathChildrenCache cache = new PathChildrenCache(client, basePath, true);
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                nodes = new HashSet<String>(client.getChildren().forPath(basePath));
            }
        });

        nodes = new HashSet<String>(client.getChildren().forPath(basePath));
    }

    public void refreshZkTask(){
        // todo....
    }

    public Set<String> nodes() {
        return nodes;
    }

    public Set<AbstractTask> tasks(){
        return null;
    }

    public Boolean createProvisionalPath(String path) {
        if (!checkePath(path)) {
            return false;
        }
        try {
            if (null != client.checkExists().forPath(path)) {
                return true;
            }
            client.create().withMode(CreateMode.EPHEMERAL).forPath(path);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 递归创建路径
     * 如果路径存在,直接返回true
     * 如果不存在,递归创建
     *
     * @param path
     * @return
     */
    public Boolean createPath(String path) {
        if (!checkePath(path)) {
            return false;
        }
        try {
            if (null != client.checkExists().forPath(path)) {
                return true;
            }
            String[] pathArr = pathArray(path);
            if (pathArr.length > 1) {
                String parentPath = arrayPath(Arrays.copyOf(pathArr, pathArr.length - 1));
                createPath(parentPath);
            }
            client.create().forPath(path);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private String arrayPath(String[] pathArray) {
        String path = "";
        for (int i = 0; i < pathArray.length; i++) {
            if (pathArray[i].length() == 0) {
                continue;
            }
            path += "/";
            path += pathArray[i];
        }
        return path;
    }

    private String[] pathArray(String path) {
        path = path.substring(1, path.length() - 1);
        return path.split("/");
    }

    public Boolean checkePath(String path) {
        if (!path.substring(0, 1).equals("/")) {
            return false;
        }
        return true;
    }
}
