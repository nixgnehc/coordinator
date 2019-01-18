package cn.bbqiu.middleware.coordinator;

/**
 * @author: nixgnehc nixgnehc@163.com
 * @date: 19-1-16
 * @time: 下午4:41
 * @Description: TODO..
 */

public interface NotfiyCallBack {

    public void call(String task, ReBalanceType type);
}
