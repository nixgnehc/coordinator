package cn.bbqiu.middleware.test;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.testng.annotations.Test;

import java.util.List;

/**
* @author: nixgnehc nixgnehc@163.com
 * @date: 19-1-16
 * @time: 上午11:11
 * @Description: TODO..
 */

@Test
public class ZkLearn {

    private CuratorFramework client;

    public ZkLearn() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.newClient("127.0.0.1", retryPolicy);
        client.start();
    }

    @Test
    public void l() throws Exception {

        List<String> list = client.getChildren().forPath("/");

//        Boolean r = delete("/middleware");
        list = client.getChildren().forPath("/");
        client.create().forPath("/middleware");
        client.create().forPath("/middleware/task");
        client.create().forPath("/middleware/task/1");
        client.create().forPath("/middleware/task/1/asdfasdf");
        client.create().forPath("/middleware/task/1/12312");
        System.out.println(list);
    }

    private Boolean delete(String path) {
        try {
            client.getChildren().forPath(path).stream().forEach(x -> {
                delete(String.format("%s/%s", path, x));
            });
            client.delete().forPath(path);
            Stat stat = client.checkExists().forPath(path);
            if (null == stat) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    @Test
    public void tl() throws Exception {
        NodeCache nodeCache = new NodeCache(client, "/middleware", false);
        nodeCache.start(true);
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                System.out.println(client.getChildren().forPath("/"));
            }
        });
        delete("/middleware");
        Thread.sleep(100000000);
    }

    @Test
    public void tint(){
        Integer i = 5/3;
        System.out.println(i);
    }
}
