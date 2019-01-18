package cn.bbqiu.middleware.coordinator;

import java.util.List;
import java.util.Set;

/**
 * @author: nixgnehc nixgnehc@163.com
 * @date: 19-1-15
 * @time: 下午10:19
 * @Description: TODO..
 */

public abstract class RefreshTask {

    /**
     * 任务更新频率, 单位ms
     */
    private Integer rate;

    public RefreshTask(){
        this(60);
    }

    public RefreshTask(Integer rate){
        this.rate = rate;
    }

    /**
     * 任务加载方法,定期执行
     * ps:需要加载说有的方法
     */
    public abstract Set<String> refresh();

    public Integer getRate() {
        return rate;
    }
}
