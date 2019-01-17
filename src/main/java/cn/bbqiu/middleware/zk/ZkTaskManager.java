package cn.bbqiu.middleware.zk;

import cn.bbqiu.middleware.AbstractTaskManager;
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
    public boolean createTask(String task) {
        String taskPath = String.format("%s/%s", zkDefine.getTaskBasePath(), task);
        boolean result = zkBiz.createPath(taskPath);
        logger.debug(String.format("create task, path:%s, and result:%b", taskPath, result));
        return result;
    }


    @Override
    public boolean deleteTask(String task) {
        String taskPath = String.format("%s/%s", zkDefine.getTaskBasePath(), task);
        logger.debug(String.format("delete task, path:%s", taskPath));
        return zkBiz.deletePath(taskPath);
    }

}
