package cn.bbqiu.middleware.zookeeper;

import cn.bbqiu.middleware.AbstractReBalance;
import cn.bbqiu.middleware.Local;

/**
* @author: nixgnehc nixgnehc@163.com
 * @date: 19-1-17
 * @time: 上午12:21
 * @Description: TODO..
 */

public class ZkReBalance extends AbstractReBalance {

    public ZkReBalance(Local local) {
        super(local);
    }


    @Override
    public Boolean lose(String task) {
        try {
            ((ZkLocal)local).getLocalTaskLockMap().get(task).release();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public Boolean scramble(String task) {
        return null;
    }
}
