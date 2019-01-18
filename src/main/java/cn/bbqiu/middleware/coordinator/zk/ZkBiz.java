package cn.bbqiu.middleware.coordinator.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

import java.util.Arrays;

/**
 * @author: nixgnehc nixgnehc@163.com
 * @date: 19-1-16
 * @time: 上午11:04
 * @Description: TODO..
 */

public class ZkBiz {

    private CuratorFramework client;

    public ZkBiz(CuratorFramework client) {
        this.client = client;
    }

    /**
     * 递归删除路径
     *
     * @param path
     * @return
     */
    public Boolean deletePath(String path) {
        if (!checkePath(path)) {
            return false;
        }
        try {
            if (null == client.checkExists().forPath(path)) {
                return true;
            }
            client.getChildren().forPath(path).stream().forEach(x -> {
                deletePath(String.format("%s/%s", path, x));
            });
            client.delete().forPath(path);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
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
