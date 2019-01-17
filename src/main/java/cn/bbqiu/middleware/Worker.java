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
     * Coordinator中的task 刷新
     */
    public void refreshTask();

    /**
     * 节点发生变化
     */
    public void doPeersRefresh();

    /**
     * 注册当前节点
     */
    public void registerLocalPeer();

    /**
     * 任务发生变化
     */
    public void doTaskRefresh();

    /**
     * 触发再平衡
     */
    public void balance();

    /**
     * 本地数据刷新
     */
    public void doLocalRefresh();

}
