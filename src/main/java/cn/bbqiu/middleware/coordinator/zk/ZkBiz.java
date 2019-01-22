package cn.bbqiu.middleware.coordinator.zk;

import cn.bbqiu.middleware.coordinator.Task;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.*;

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

    private List<Task> tasks;

    private Gson gson = new Gson();
    private volatile boolean waitUpdate = false;

    public ZkBiz(String hosts, String basePath, String local) throws Exception {
        this.basePath = basePath;
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.newClient(hosts, retryPolicy);
        client.start();
        createPath(basePath);
        createProvisionalPath(String.format("%s/peers/%s", basePath, local));

        PathChildrenCache cache = new PathChildrenCache(client, basePath, true);
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                nodes = new HashSet<String>(client.getChildren().forPath(basePath));
            }
        });

        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                waitUpdate = true;
            }
        });

        nodes = new HashSet<String>(client.getChildren().forPath(basePath));
    }

    public void refreshZkTask(List<Task> tasks) {
        Map<String, Task> tasksMap = Maps.newConcurrentMap();
        tasks.stream().forEach(x -> {
            tasksMap.put(x.getIdentify(), x);
        });

        List<Task> zkTasks = allInZkTasks();

        Map<String, Task> zkTaskMap = Maps.newConcurrentMap();
        zkTasks.stream().forEach(x -> {
            zkTaskMap.put(x.getIdentify(), x);
        });

        // 不在zk中，新增
        tasksMap.entrySet().stream().forEach(x -> {
            String taskPath = String.format("%s/tasks/%s", basePath, x.getValue().getIdentify());
            if (!zkTaskMap.containsKey(x.getKey())) {
                try {
                    client.create().forPath(taskPath);
                    // todo... 新增data
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // 不在新刷新到的列表中，删除
        zkTaskMap.entrySet().stream().forEach(x -> {
            if (!tasksMap.containsKey(x.getKey())) {
                String taskPath = String.format("%s/tasks/%s", basePath, x.getValue().getIdentify());
                try {
                    client.delete().forPath(taskPath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    public Set<String> nodes() {
        return nodes;
    }

    public List<Task> allInZkTasks() {
        synchronized (this) {
            if (!waitUpdate) {
                return tasks;
            }
            List<String> taskIdentifys;
            waitUpdate = false;
            try {
                taskIdentifys = client.getChildren().forPath(String.format("%s/tasks/", basePath));
                tasks.clear();
                taskIdentifys.stream().forEach(x -> {
                    try {
                        byte[] b = client.getData().forPath(String.format("%s/tasks/%s", basePath, x));
                        tasks.add(gson.fromJson(new String(b), new TypeToken<Task>() {
                        }.getType()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tasks;
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
