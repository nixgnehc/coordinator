package cn.bbqiu.middleware.zookeeper;

import cn.bbqiu.middleware.Local;
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

    private Map<String, InterProcessSemaphoreMutex> localTaskLockMap = Maps.newHashMap();

    private ZkDefine zkDefine;

    private CuratorFramework client;

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

    public Map<String, InterProcessSemaphoreMutex> getLocalTaskLockMap() {
        return localTaskLockMap;
    }

}
