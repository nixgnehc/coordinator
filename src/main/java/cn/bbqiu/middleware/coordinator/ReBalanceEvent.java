package cn.bbqiu.middleware.coordinator;

/**
 * @author: nixgnehc nixgnehc@163.com
 * @date: 19-1-15
 * @time: 下午10:30
 * @Description: TODO..
 */

public class ReBalanceEvent {

    private ReBalanceType type;

    private String task;

    public ReBalanceType getType() {
        return type;
    }

    public void setType(ReBalanceType type) {
        this.type = type;
    }


    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }
}
