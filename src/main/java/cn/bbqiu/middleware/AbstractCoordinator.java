package cn.bbqiu.middleware;

import cn.bbqiu.middleware.utils.LocalUtil;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: nixgnehc nixgnehc@163.com
 * @date: 19-1-16
 * @time: 下午5:40
 * @Description: TODO..
 */

public abstract class AbstractCoordinator implements Coordinator, Worker {


    private Logger logger = LoggerFactory.getLogger(AbstractCoordinator.class);

    public String peerName = "";

    public Local local;

    public int rebalanceFrequency = 30;

    public boolean waitRebalance = false;

    private List<BusinessListener> listeners = Lists.newArrayList();

    public CoordinatorTaskLoading taskLoad;

    protected ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(5);

    @Override
    public void register(BusinessListener listener) {
        listeners.add(listener);
    }

    public ReBalance reBalance;

    @Override
    public void start(CoordinatorTaskLoading load) {
        String sign = LocalUtil.sign();
        this.start(load, sign);
    }

    @Override
    public void start(CoordinatorTaskLoading load, String localSign, Integer rebalanceFrequency) {
        String sign = LocalUtil.sign();
        this.rebalanceFrequency = rebalanceFrequency;

        this.start(load, sign);
    }


    @Override
    public void start(CoordinatorTaskLoading load, String peerName) {
        this.peerName = peerName;
        this.taskLoad = load;


        init();
        registerLocalPeer();
        refreshTask();


        doTaskRefresh();
        doPeersRefresh();
        doBalance();
    }

    public void doBalance() {
        scheduled.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (waitRebalance) {
                    logger.debug(String.format("触发数据刷新"));
                    waitRebalance = false;
                    doLocalRefresh();
                    reBalance.coordinatorTaskChange(callBack, local);
                    reBalance.localLose(callBack, local);
                    reBalance.localScramble(callBack, local);
                }
            }
        }, rebalanceFrequency, rebalanceFrequency, TimeUnit.SECONDS);
    }

    @Override
    public void balance() {
        waitRebalance = true;
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
