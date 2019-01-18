package cn.bbqiu.middleware.coordinator;

/**
 * @author: nixgnehc nixgnehc@163.com
 * @date: 19-1-15
 * @time: 下午10:30
 * @Description: TODO..
 */

public enum ReBalanceType {
    // 丢失任务
    Lose(),
    // 新获取到任务
    Obtain();

    private ReBalanceType() {
    }
}
