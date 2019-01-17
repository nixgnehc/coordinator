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
     * 初始化
     * @param task
     */
    public void start(CoordinatorTaskLoading task);

    /**
     * 任务变化调用listener,多个可以重复注册
     * @param listener
     */
    public void register(BusinessListener listener);


    /**
     * 当前本地获取到的失误
     * @return
     */
    public List<String> currentLocalTask();
}
