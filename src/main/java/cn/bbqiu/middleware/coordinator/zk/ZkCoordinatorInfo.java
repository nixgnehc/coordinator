package cn.bbqiu.middleware.coordinator.zk;

import cn.bbqiu.middleware.coordinator.CoordinatorInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: chengxin chengxin@zbj.com
 * @date: 19-1-21
 * @time: 下午11:18
 * @Description: TODO..
 */

@Getter
@Setter
public class ZkCoordinatorInfo extends CoordinatorInfo {

    private String hosts;

    private String basePath;


}
