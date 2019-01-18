package cn.bbqiu.middleware.coordinator;

import cn.bbqiu.middleware.coordinator.peers.PeerInfoStrategy;
import cn.bbqiu.middleware.coordinator.peers.ipstrategy.IpPeerInfoStrategy;
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

    public int rebalanceFrequency;

    public boolean waitRebalance = false;

    private List<BusinessListener> listeners = Lists.newArrayList();

    public RefreshTask taskLoad;

    protected ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(5);

    @Override
    public void register(BusinessListener listener) {
        listeners.add(listener);
    }

    public ReBalance reBalance;

    private PeerInfoStrategy peerInfoStrategy;

    @Override
    public void start(RefreshTask load, Integer rebalanceFrequency) {
        peerInfoStrategy = new IpPeerInfoStrategy();

        start(load, peerInfoStrategy.name(), rebalanceFrequency);
    }

    @Override
    public void start(RefreshTask load, String peerName, Integer rebalanceFrequency) {

        this.taskLoad = load;
        this.rebalanceFrequency = rebalanceFrequency;
        this.peerName = peerName;


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
        public void call(String task, ReBalanceType type) {
            ReBalanceEvent event = new ReBalanceEvent();
            event.setTask(task);
            event.setType(type);

            listeners.stream().forEach(x -> {
                x.call(event);
            });
        }
    };


}
