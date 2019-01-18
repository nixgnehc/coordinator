package cn.bbqiu.middleware.coordinator.test.integration;

import cn.bbqiu.middleware.coordinator.Coordinator;
import cn.bbqiu.middleware.coordinator.RefreshTask;
import cn.bbqiu.middleware.coordinator.zk.ZkCoordinator;
import com.google.gson.Gson;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.collections.Lists;
import org.testng.collections.Sets;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: chengxin chengxin@zbj.com
 * @date: 19-1-17
 * @time: 下午4:36
 * @Description: TODO..
 */

public class SignleNodeDebugApp {

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHH");

    private Logger logger = LoggerFactory.getLogger(SignleNodeDebugApp.class);

    private Gson gson = new Gson();
    @Getter
    private Coordinator coordinator = new ZkCoordinator("127.0.0.1", "/coordinator/test", 10);

    public static void main(String[] args) {
        SignleNodeDebugApp app = new SignleNodeDebugApp();
        app.getCoordinator().start(app.refreshTask(), String.valueOf(System.currentTimeMillis()), 10);
    }


    public RefreshTask refreshTask() {
        return new RefreshTask() {
            @Override
            public Set<String> refresh() {
                Long taskNum = System.currentTimeMillis() % 20 + 2;
                Set<String> tasks = Sets.newHashSet();
                Date date = new Date();
                for (long i = 0; i <= taskNum; i++) {
                    tasks.add(String.format("%s%dt", formatter.format(date), i));
                }
                logger.debug(String.format("refresh task:%s", gson.toJson(tasks)));
                return tasks;
            }
        };
    }

}
