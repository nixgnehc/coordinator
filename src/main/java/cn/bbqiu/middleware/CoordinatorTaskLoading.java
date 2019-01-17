package cn.bbqiu.middleware;

import java.util.List;

/**
 * @author: nixgnehc nixgnehc@163.com
 * @date: 19-1-15
 * @time: 下午10:19
 * @Description: TODO..
 */

public abstract class CoordinatorTaskLoading {

    /**
     * 任务更新频率, 单位ms
     */
    private Integer rate;

    public CoordinatorTaskLoading(){
        this(60);
    }

    public CoordinatorTaskLoading(Integer rate){
        this.rate = rate;
    }

    /**
     * 任务加载方法,定期执行
     * ps:需要加载说有的方法
     */
    public abstract List<String> loading();

    public Integer getRate() {
        return rate;
    }
}
