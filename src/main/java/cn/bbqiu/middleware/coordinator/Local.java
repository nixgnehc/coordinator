package cn.bbqiu.middleware.coordinator;

import java.util.List;

/**
 * @author: nixgnehc nixgnehc@163.com
 * @date: 19-1-16
 * @time: 下午4:58
 * @Description: TODO..
 */

public abstract class Local {


    private List<String> locaTask;

    private List<String> allTast;

    private Integer peerNum;


    public void setLocaTask(List<String> locaTask) {
        this.locaTask = locaTask;
    }

    public void setPeerNum(Integer peerNum) {
        this.peerNum = peerNum;
    }

    public Integer getMaxTask() {
        int taskNum = allTast.size();
        int maxTask = taskNum / peerNum;
        if (0 != taskNum % peerNum) {
            maxTask++;
        }
        return maxTask;
    }


    public List<String> getLocaTask() {
        return locaTask;
    }

    public List<String> getAllTast() {
        return allTast;
    }

    public void setAllTast(List<String> allTast) {
        this.allTast = allTast;
    }
}
