package cn.bbqiu.middleware;

import java.util.List;

/**
 * @author: nixgnehc nixgnehc@163.com
 * @date: 19-1-15
 * @time: 下午10:16
 * @Description: 协调器,供业务使用
 */

public interface Coordinator {

    /**
     * 启动
     * @param task
     */
    public void start(CoordinatorTaskLoading task);


    public void start(CoordinatorTaskLoading task, String localSign);

    public void start(CoordinatorTaskLoading task, String localSign, Integer rebalanceFrequency);

    /**
     * 任务变化调用listener,多个可以重复注册
     * @param listener
     */
    public void register(BusinessListener listener);


}
