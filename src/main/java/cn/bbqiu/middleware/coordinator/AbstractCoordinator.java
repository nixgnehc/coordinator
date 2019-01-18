package cn.bbqiu.middleware.coordinator;

import cn.bbqiu.middleware.coordinator.nodes.NodesInfoStrategy;
import cn.bbqiu.middleware.coordinator.nodes.ipstrategy.IpNodeInfoStrategy;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
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

    public String nodeName = "";

    public Local local;

    public int rebalanceFrequency;

    public boolean waitRebalance = false;

    private Gson gson = new Gson();

    private List<BusinessListener> listeners = Lists.newArrayList();

    public RefreshTask taskLoad;

    protected ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(5);

    @Override
    public void register(BusinessListener listener) {
        listeners.add(listener);
    }

    public ReBalance reBalance;

    private NodesInfoStrategy peerInfoStrategy;

    @Override
    public void start(RefreshTask load, Integer rebalanceFrequency) {
        peerInfoStrategy = new IpNodeInfoStrategy();

        start(load, peerInfoStrategy.name(), rebalanceFrequency);
    }

    @Override
    public void start(RefreshTask load, String nodeName, Integer rebalanceFrequency) {

        this.taskLoad = load;
        this.rebalanceFrequency = rebalanceFrequency;
        this.nodeName = nodeName;


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
                logger.debug(String.format("触发本地数据数据刷新"));
                try {
                    if (waitRebalance) {
                        waitRebalance = false;
                        doLocalRefresh();
                        reBalance.coordinatorTaskChange(callBack, local);
                        reBalance.localLose(callBack, local);
                        reBalance.localScramble(callBack, local);
                        logger.debug(String.format("local task:%s", gson.toJson(local.getLocaTask())));
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    logger.error("doBalance happend error:", e);
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


    @Override
    public List<String> localTask() {
        return local.getLocaTask();
    }
}
