package cn.bbqiu.middleware;

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
    public void start(CoordinatorTaskLoading load) {
        this.taskLoad = load;
        init();
        coordinatorTask();
        peers();
        localTask();
    }

    @Override
    public void balance(ReBalanceSource source) {

        // todo...
//        reBalances.stream().forEach(x->{
//            x.balance(callBack, local);
//        });
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
}
