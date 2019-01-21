package cn.bbqiu.middleware.coordinator.zk;

import cn.bbqiu.middleware.coordinator.AbstractTaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: nixgnehc nixgnehc@163.com
 * @date: 19-1-16
 * @time: 下午4:20
 * @Description: TODO..
 */

public class ZkTaskManager extends AbstractTaskManager {

    private Logger logger = LoggerFactory.getLogger(ZkTaskManager.class);

    private ZkLocal zkLocal;
//
//    private ZkBiz zkBiz;
//
//    private ZkDefine zkDefine;

    public ZkTaskManager(ZkLocal zkLocal) {
        this.zkLocal = zkLocal;
//        this.zkBiz = zkBiz;
//        this.zkDefine = zkDefine;
    }

    /**
     * 创建task
     *
     * @param task
     */
    @Override
    public boolean createTask(String task) {

        String taskPath = String.format("%s/%s", zkLocal.getZkDefine().getTaskBasePath(), task);
        boolean result = zkLocal.getZkBiz().createPath(taskPath);
        logger.debug(String.format("create task, path:%s, and result:%b", taskPath, result));
        return result;
    }


    @Override
    public boolean deleteTask(String task) {
        String taskPath = String.format("%s/%s", zkLocal.getZkDefine().getTaskBasePath(), task);
        logger.debug(String.format("delete task, path:%s", taskPath));
        return zkLocal.getZkBiz().deletePath(taskPath);
    }

}
