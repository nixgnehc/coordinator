package cn.bbqiu.middleware.coordinator.test.zk;

import cn.bbqiu.middleware.coordinator.test.utils.ZKUtil;
import cn.bbqiu.middleware.coordinator.zk.ZkBiz;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: chengxin chengxin@zbj.com
 * @date: 19-1-17
 * @time: 下午4:48
 * @Description: TODO..
 */

public class ZkBizTest {

    ZkBiz zkBiz = new ZkBiz(ZKUtil.client());

    @Test
    public void checkePathTest() {

        Assert.assertTrue(zkBiz.checkePath("/a/b/c"));

    }


    @Test
    public void clean() throws Exception {
        String bashPath = "/";
        CuratorFramework client = ZKUtil.client();
        this.removeAll(bashPath, client);
    }

    private void removeAll(String basePath, CuratorFramework client){
        List<String> list = null;
        if (basePath.equals("/zookeeper")) {
            return;
        }
        try {
            list = client.getChildren().forPath(basePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list != null) {
            list.stream().forEach(x->{
                String child = "";
                if (basePath.equals("/")) {
                    child = String.format("/%s", x);
                } else {
                    child = String.format("%s/%s", basePath, x);
                }
                removeAll(child, client);
            });
        }
        try {
            if (!basePath.equals("/")) {
                client.delete().forPath(basePath);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void listenerTest() throws Exception {
        CuratorFramework client = ZKUtil.client();
        removeAll("/", client);
        client.create().forPath("/a");
        client.create().forPath("/b");
        PathChildrenCache nodeCache = new PathChildrenCache(client, "/a", true);
        nodeCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                System.out.println("nodeCache2");
                System.out.println(event);
            }

        });


        PathChildrenCache nodeCache1 = new PathChildrenCache(client, "/b", true);
        nodeCache1.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                System.out.println("nodeCache1");
                System.out.println(event);
            }

        });

        nodeCache.start();
        nodeCache1.start();

        new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    client.create().forPath(String.format("/b/%d", System.currentTimeMillis()));
                    client.create().forPath(String.format("/a/%d", System.currentTimeMillis()));
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        }, 5, 5, TimeUnit.SECONDS);


        Thread.sleep(10000000);
    }
}
