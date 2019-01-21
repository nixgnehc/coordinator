package cn.bbqiu.middleware.coordinator;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: chengxin chengxin@zbj.com
 * @date: 19-1-21
 * @time: 下午10:54
 * @Description: TODO..
 */

@Getter
@Setter
public abstract class CoordinatorInfo {


    private int virtualMultiple;
    private int taskRefreshFrequency;
}
