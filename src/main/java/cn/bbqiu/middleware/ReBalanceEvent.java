package cn.bbqiu.middleware;

/**
 * @author: nixgnehc nixgnehc@163.com
 * @date: 19-1-15
 * @time: 下午10:30
 * @Description: TODO..
 */

public class ReBalanceEvent {

    private Type type;

    private String task;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }


    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }
}
