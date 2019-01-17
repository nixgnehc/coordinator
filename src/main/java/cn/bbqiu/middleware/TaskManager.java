package cn.bbqiu.middleware;

import java.util.List;

/**
 * @author: nixgnehc nixgnehc@163.com
 * @date: 19-1-16
 * @time: 下午4:19
 * @Description: 任务变更时候使用到的任务管理
 */

public interface TaskManager {

    /**
     * 新增任务
     *
     * @param task
     */
    public void createTask(String task);

    /**
     * 删除任务
     *
     * @param task
     */
    public void deleteTask(String task);

    /**
     * 修订任务
     */
    public void revise(List<String> currentTask, List<String> coordinatorTask);
}