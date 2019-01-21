package cn.bbqiu.middleware.coordinator.zk;

import cn.bbqiu.middleware.coordinator.AbstractTask;
import cn.bbqiu.middleware.coordinator.CallBack;
import cn.bbqiu.middleware.coordinator.Coordinator;
import cn.bbqiu.middleware.coordinator.CoordinatorInfo;
import cn.bbqiu.middleware.coordinator.local.IPAddress;
import cn.bbqiu.middleware.coordinator.local.IPAddressUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Builder;

import java.net.Inet4Address;
import java.net.SocketException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: chengxin chengxin@zbj.com
 * @date: 19-1-21
 * @time: 下午10:25
 * @Description: TODO..
 */

public abstract class ZkCoordinator implements Coordinator {

    private final Integer poolCoreSize = Runtime.getRuntime().availableProcessors() * 2;
    private Map<String, AbstractTask> localTasks = Maps.newHashMap();
    private final ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(poolCoreSize);

    private List<CallBack> callBacks = Lists.newArrayList();

    private ZkBiz zkBiz;

    private ZkCoordinatorInfo info;

    public ZkCoordinator(ZkCoordinatorInfo zkInfo) {
        this.info = zkInfo;
    }

    @Override
    public void init() throws Exception {

        IPAddress ipAddress = IPAddressUtil.loadLocalAddress();
        Inet4Address inet4Address = ipAddress.getIpv4AddressList().get(0);

        zkBiz = new ZkBiz(info.getHosts(), info.getBasePath(), inet4Address.getHostAddress());

        start();
    }


    @Override
    public void start() throws RuntimeException {
        if (callBacks.size() < 1) {
            new RuntimeException("Callback is empty, 必须在启动之前注册Callback");
        }
        scheduled.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    List<AbstractTask> tasks = refresh();
                    // todo.... if locked , update tasks;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ;
            }
        }, 0, info.getTaskRefreshFrequency(), TimeUnit.SECONDS);

        scheduled.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                Set<String> nodes = zkBiz.nodes();
                Set<AbstractTask> tasks = zkBiz.tasks();
                //todo ... localtask


                callBacks.stream().forEach(x->{
//                    x.start(tasks.get(0));
                });
            }
        }, 0 , info.getTaskRefreshFrequency(), TimeUnit.SECONDS);
    }

    @Override
    public abstract List<AbstractTask> refresh();

    @Override
    public void registerCallback(CallBack callBack) {
        this.callBacks.add(callBack);
    }

}
