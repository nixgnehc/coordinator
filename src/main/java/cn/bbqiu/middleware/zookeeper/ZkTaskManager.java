package cn.bbqiu.middleware.zookeeper;

import cn.bbqiu.middleware.AbstractTaskManager;

/**
* @author: nixgnehc nixgnehc@163.com
 * @date: 19-1-16
 * @time: 下午4:20
 * @Description: TODO..
 */

public class ZkTaskManager extends AbstractTaskManager {

    private ZkBiz zkBiz;

    private ZkDefine zkDefine;

    public ZkTaskManager(ZkBiz zkBiz, ZkDefine zkDefine) {
        this.zkBiz = zkBiz;
        this.zkDefine = zkDefine;
    }

    /**
     * 创建task
     *
     * @param task
     */
    @Override
    public void createTask(String task) {
        zkBiz.createPath(String.format("%s/%s", zkDefine.getTaskBasePath(), task));
    }


    @Override
    public void deleteTask(String task) {
        zkBiz.deletePath(String.format("%s/%s", zkDefine.getTaskBasePath(), task));
    }



}
