package cn.bbqiu.middleware.zk;

import cn.bbqiu.middleware.AbstractReBalance;
import cn.bbqiu.middleware.Local;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;

import java.util.concurrent.TimeUnit;

/**
 * @author: nixgnehc nixgnehc@163.com
 * @date: 19-1-17
 * @time: 上午12:21
 * @Description: TODO..
 */

public class ZkReBalance extends AbstractReBalance {

    private ZkLocal zkLocal;

    public ZkReBalance(Local local) {
        zkLocal = (ZkLocal) local;
    }


    @Override
    public Boolean lose(String task) {
        try {
            zkLocal.getLocalTaskLockMap().get(task).release();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public Boolean scramble(String task) {
        String taskPath = String.format("%s/%s", zkLocal.getZkDefine().getTaskBasePath(), task);
        InterProcessSemaphoreMutex lock = new InterProcessSemaphoreMutex(zkLocal.getClient(), zkLocal.getZkDefine().getLockBasePath());
        try {
            if (lock.acquire(0, TimeUnit.SECONDS)){
                zkLocal.getLocaTask().add(task);
                zkLocal.getLocalTaskLockMap().put(task, lock);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
