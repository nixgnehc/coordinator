package cn.bbqiu.middleware.coordinator;

/**
 * @author: nixgnehc nixgnehc@163.com
 * @date: 19-1-16
 * @time: 上午11:24
 * @Description: TODO..
 */

public class TaskSurvivalEntity {

    private String task;
    private Boolean inCoordinator;
    private Boolean inCurrent;

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public Boolean getInCurrent() {
        return inCurrent;
    }

    public void setInCurrent(Boolean inCurrent) {
        this.inCurrent = inCurrent;
    }

    public Boolean getInCoordinator() {
        return inCoordinator;
    }

    public void setInCoordinator(Boolean inCoordinator) {
        this.inCoordinator = inCoordinator;
    }
}
