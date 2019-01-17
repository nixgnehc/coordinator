package cn.bbqiu.middleware;

/**
 * @author: nixgnehc nixgnehc@163.com
 * @date: 19-1-16
 * @time: 下午11:14
 * @Description: 协调中工作定义,协调器运作流程
 */

public interface Worker {

    /**
     * 初始化
     */
    public void init();

    /**
     * Coordinator中的task
     */
    public void coordinatorTask();

    /**
     * 节点
     */
    public void peers();


    /**
     * 当前节点task
     */
    public void localTask();

    /**
     * 触发再平衡,通知业务程序
     */
    public void balance(ReBalanceSource source);

    /**
     * 进入维护状态
     */
    public void maintenance();

    /**
     * 进入工作状态中
     */
    public void atwork();
}
