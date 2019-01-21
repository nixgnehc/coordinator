package cn.bbqiu.middleware.coordinator;

import java.util.List;

/**
 * @author: chengxin chengxin@zbj.com
 * @date: 19-1-21
 * @time: 下午10:25
 * @Description: TODO..
 */

public class ZkCoordinator implements Coordinator{
    private Integer virtualMultiple;

    @Override
    public void start(int virtualMultiple) {
        this.virtualMultiple = virtualMultiple;
    }

    @Override
    public List<AbstractTask> refresh() {

        return null;
    }

    @Override
    public List<AbstractTask> localTask() {
        return null;
    }
}
