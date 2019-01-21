package cn.bbqiu.middleware.coordinator.zk;

import cn.bbqiu.middleware.coordinator.Local;
import com.google.common.collect.Maps;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;

import java.util.Map;

/**
* @author: nixgnehc nixgnehc@163.com
 * @date: 19-1-16
 * @time: 下午5:00
 * @Description: TODO..
 */

public class ZkLocal extends Local {

    private Map<String, Boolean> localTaskLockMap = Maps.newHashMap();

    private ZkDefine zkDefine;

    private CuratorFramework client;

    public ZkBiz getZkBiz() {
        return zkBiz;
    }

    public void setZkBiz(ZkBiz zkBiz) {
        this.zkBiz = zkBiz;
    }

    private ZkBiz zkBiz;

    public CuratorFramework getClient() {
        return client;
    }

    public void setClient(CuratorFramework client) {
        this.client = client;
    }

    public ZkDefine getZkDefine() {
        return zkDefine;
    }

    public void setZkDefine(ZkDefine zkDefine) {
        this.zkDefine = zkDefine;
    }

    public Map<String, Boolean> getLocalTaskLockMap() {
        return localTaskLockMap;
    }
}
