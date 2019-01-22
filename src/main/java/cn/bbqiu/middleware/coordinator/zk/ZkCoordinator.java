package cn.bbqiu.middleware.coordinator.zk;

import cn.bbqiu.middleware.coordinator.Task;
import cn.bbqiu.middleware.coordinator.CallBack;
import cn.bbqiu.middleware.coordinator.Coordinator;
import cn.bbqiu.middleware.coordinator.local.IPAddress;
import cn.bbqiu.middleware.coordinator.local.IPAddressUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.net.Inet4Address;
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
    private Map<String, Task> localTasks = Maps.newHashMap();
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
                    zkBiz.refreshZkTask(refresh());
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
                List<Task> zkTasks = zkBiz.allInZkTasks();

                // todo...
//                List<Task> newTask = Lists.newArrayList();
//                zkTasks.stream().forEach(x -> {
//                    if (!localTasks.containsKey(x.getIdentify())) {
//                        newTask.add(x);
//                    }
//                });
//
//                localTasks.entrySet().stream().forEach(x->{
//                    if (zkTasks.)
//                });
//                newTask.stream().forEach(x->{
//                    callBacks.stream().forEach(y->{
//                        y.start(x);
//                        localTasks.put(x.getIdentify(), x);
//                    });
//                });
//
//                List<Task> destoryTask = Lists.newArrayList();
//
//                callBacks.stream().forEach(x -> {
////                    x.start(tasks.get(0));
//                });
            }
        }, 0, info.getTaskRefreshFrequency(), TimeUnit.SECONDS);
    }

    @Override
    public abstract List<Task> refresh();

    @Override
    public void registerCallback(CallBack callBack) {
        this.callBacks.add(callBack);
    }

}
