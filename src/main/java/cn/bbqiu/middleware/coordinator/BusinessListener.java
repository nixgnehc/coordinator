package cn.bbqiu.middleware.coordinator;

/**
 * @author: nixgnehc nixgnehc@163.com
 * @date: 19-1-15
 * @time: 下午10:20
 * @Description: TODO..
 */

public interface BusinessListener {

    public void call(ReBalanceEvent event);
}
