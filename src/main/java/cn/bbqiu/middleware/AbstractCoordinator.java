package cn.bbqiu.middleware;

import cn.bbqiu.middleware.utils.LocalUtil;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @author: nixgnehc nixgnehc@163.com
 * @date: 19-1-16
 * @time: 下午5:40
 * @Description: TODO..
 */

public abstract class AbstractCoordinator implements Coordinator, Worker {

    public Boolean maintenanceSign = false;

    public String peerSign = "";

    public Local local;

    private List<BusinessListener> listeners = Lists.newArrayList();

    public CoordinatorTaskLoading taskLoad;

    protected ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(10);

    @Override
    public List<String> currentLocalTask() {
        return local.getLocaTask();
    }

    @Override
    public void register(BusinessListener listener) {
        listeners.add(listener);
    }

    public ReBalance reBalance;

    @Override
    public void start(CoordinatorTaskLoading task) {
        String sign = LocalUtil.sign();
        this.start(task, sign);
    }

    @Override
    public void start(CoordinatorTaskLoading load, String peerSign) {
        this.peerSign = peerSign;
        this.taskLoad = load;
        init();
        coordinatorTask();
        peers();
        localTask();
    }

    @Override
    public void balance(ReBalanceSource source) {

        if (source.equals(ReBalanceSource.TASK)) {
            reBalance.coordinatorTaskChange(callBack, local);
        }
        reBalance.localLose(callBack, local);
        reBalance.localScramble(callBack, local);

    }

    /**
     * 通知业务
     */
    private NotfiyCallBack callBack = new NotfiyCallBack() {
        @Override
        public void call(String task, Type type) {
            ReBalanceEvent event = new ReBalanceEvent();
            event.setTask(task);
            event.setType(type);

            listeners.stream().forEach(x -> {
                x.call(event);
            });
        }
    };


    @Override
    public Boolean maintain(Boolean isMaintain) {
        if (isMaintain) {
            this.maintenance();
            this.maintenanceSign = true;
        } else {
            this.atwork();
            this.maintenanceSign = false;
        }
        return isMaintain;
    }
}
