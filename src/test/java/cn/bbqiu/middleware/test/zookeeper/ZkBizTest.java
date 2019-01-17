package cn.bbqiu.middleware.test.zookeeper;

import cn.bbqiu.middleware.test.utils.ZKUtil;
import cn.bbqiu.middleware.zookeeper.ZkBiz;
import org.apache.curator.framework.CuratorFramework;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author: chengxin chengxin@zbj.com
 * @date: 19-1-17
 * @time: 下午4:48
 * @Description: TODO..
 */

public class ZkBizTest {

    ZkBiz zkBiz = new ZkBiz(ZKUtil.client());

    @Test
    public void checkePathTest(){

        Assert.assertTrue(zkBiz.checkePath("/a/b/c"));

    }


    @Test
    public void clean() throws Exception {
        String bashPath = "/coordinator/test/task";
        CuratorFramework client = ZKUtil.client();
        List<String> list = client.getChildren().forPath(bashPath);
        list.stream().forEach(x->{
            Assert.assertTrue(zkBiz.deletePath(String.format("%s/%s", bashPath,x)));
        });

    }
}
