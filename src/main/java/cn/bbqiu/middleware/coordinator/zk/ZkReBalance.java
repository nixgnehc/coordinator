package cn.bbqiu.middleware.coordinator.zk;

import cn.bbqiu.middleware.coordinator.AbstractReBalance;
import cn.bbqiu.middleware.coordinator.Local;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;

import java.util.concurrent.TimeUnit;

/**
 * @author: nixgnehc nixgnehc@163.com
 * @date: 19-1-17
 * @time: 上午12:21
 * @Description: TODO..
 */

public class ZkReBalance extends AbstractReBalance {

    private String LOCKVALUE = "_l_";

    private ZkLocal zkLocal;

    public ZkReBalance(Local local) {
        zkLocal = (ZkLocal) local;
    }

    @Override
    public Boolean lose(String task) {
        try {
            zkLocal.getLocalTaskLockMap().remove(task);

            zkLocal.getClient().delete().forPath(String.format("%s/%s/%s", zkLocal.getZkDefine().getTaskBasePath(), task, LOCKVALUE));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public Boolean scramble(String task) {
        String taskPath = String.format("%s/%s", zkLocal.getZkDefine().getTaskBasePath(), task);
//        InterProcessSemaphoreMutex lock = new InterProcessSemaphoreMutex(zkLocal.getClient(), taskPath);
        try {
            zkLocal.getClient().create().
//            if (lock.acquire(0, TimeUnit.SECONDS)) {
                zkLocal.getLocaTask().add(task);
                zkLocal.getLocalTaskLockMap().put(task, true);
                return true;
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
