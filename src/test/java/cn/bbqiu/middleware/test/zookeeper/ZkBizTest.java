package cn.bbqiu.middleware.test.zookeeper;

import cn.bbqiu.middleware.test.utils.ZKUtil;
import cn.bbqiu.middleware.zookeeper.ZkBiz;
import org.testng.Assert;
import org.testng.annotations.Test;

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
}
