package cn.bbqiu.middleware.coordinator;

import java.util.List;

/**
 * @author: nixgnehc nixgnehc@163.com
 * @date: 19-1-15
 * @time: 下午10:16
 * @Description: 协调器
 */

public interface Coordinator {

    public void start(int virtualMultiple);

    public List<AbstractTask> refresh();

    public void registerCallback(CallBack callBack);

    public void destory();
}
