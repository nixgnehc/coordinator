package cn.bbqiu.middleware;

/**
 * @author: nixgnehc nixgnehc@163.com
 * @date: 19-1-16
 * @time: 下午4:38
 * @Description: TODO..
 */

public interface ReBalance {

    /**
     * 释放任务
     * @return
     */
    public Boolean lose(String task);

    /**
     * 争抢任务
     * @return
     */
    public Boolean scramble(String task);

    /**
     * 由于coordinator中的task发生变化,导致需要rebalance
     * @param callback
     * @param local
     */
    public void coordinatorTaskChange(NotfiyCallBack callback, Local local);

    /**
     * 由于本地丢弃任务,需要进行rebalance
     * @param callback
     * @param local
     */
    public void localLose(NotfiyCallBack callback, Local local);

    /**
     * 由于本地持有task数量不足最大task数目,主动竞争
     * @param callback
     * @param local
     */
    public void localScramble(NotfiyCallBack callback, Local local);
}
