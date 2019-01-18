package cn.bbqiu.middleware.coordinator;

/**
 * @author: nixgnehc nixgnehc@163.com
 * @date: 19-1-15
 * @time: 下午10:16
 * @Description: 协调器,供业务使用
 */

public interface Coordinator {

    /**
     * 启动
     * @param refreshTask
     */
    public void start(RefreshTask refreshTask, Integer rebalanceFrequency);


    /**
     * 不推荐使用该方法初始化。
     * 定义该方法主要是为了方便调试。
     * @param refreshTask
     * @param name
     * @param rebalanceFrequency
     */
    public void start(RefreshTask refreshTask, String name, Integer rebalanceFrequency);

    /**
     * 任务变化调用listener,多个可以重复注册
     * @param listener
     */
    public void register(BusinessListener listener);


}
