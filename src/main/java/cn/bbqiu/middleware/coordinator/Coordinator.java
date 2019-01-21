package cn.bbqiu.middleware.coordinator;

import java.net.SocketException;
import java.util.List;

/**
 * @author: nixgnehc nixgnehc@163.com
 * @date: 19-1-15
 * @time: 下午10:16
 * @Description: 协调器
 */

public interface Coordinator {

    public void init() throws Exception;


    void start();

    /**
     * 刷新task
     * @return
     */
    public List<AbstractTask> refresh();


    public void registerCallback(CallBack callBack);

}
