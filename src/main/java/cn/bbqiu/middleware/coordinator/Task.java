package cn.bbqiu.middleware.coordinator;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author: chengxin chengxin@zbj.com
 * @date: 19-1-21
 * @time: 下午10:30
 * @Description: TODO..
 */

@Getter
@Setter
public class Task {

    public String identify;
    public Map<String, String> property;
}
