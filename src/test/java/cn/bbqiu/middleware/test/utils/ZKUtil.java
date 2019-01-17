package cn.bbqiu.middleware.test.utils;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author: chengxin chengxin@zbj.com
 * @date: 19-1-17
 * @time: 下午4:53
 * @Description: TODO..
 */

public class ZKUtil {

    public static CuratorFramework client() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client =  CuratorFrameworkFactory.newClient("127.0.0.1", retryPolicy);
        client.start();
        return client;
    }
}
