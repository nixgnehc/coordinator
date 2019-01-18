package cn.bbqiu.middleware.coordinator.test.integration;

import cn.bbqiu.middleware.coordinator.Coordinator;
import cn.bbqiu.middleware.coordinator.test.utils.CoordinatorTaskLoadingUtil;
import cn.bbqiu.middleware.coordinator.zk.ZkCoordinator;

/**
 * @author: chengxin chengxin@zbj.com
 * @date: 19-1-17
 * @time: 下午4:36
 * @Description: TODO..
 */

public class App1 {

    static Coordinator coordinator = new ZkCoordinator("127.0.0.1", "/coordinator/test", 10);

    public static void main(String[] args) {
        coordinator.start(CoordinatorTaskLoadingUtil.coordinatorTaskLoading(), String.valueOf(System.currentTimeMillis()),  10);
    }
}
