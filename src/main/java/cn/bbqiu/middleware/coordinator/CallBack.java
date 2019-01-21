package cn.bbqiu.middleware.coordinator;

/**
 * @author: chengxin chengxin@zbj.com
 * @date: 19-1-21
 * @time: 下午10:26
 * @Description: TODO..
 */

public interface CallBack {

    /**
     * start回调
     * @param task
     */
    public void start(AbstractTask task);

    /**
     * destory 回调
     * @param task
     */
    public void destory(AbstractTask task);
}
