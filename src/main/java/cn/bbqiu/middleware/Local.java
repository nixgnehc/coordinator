package cn.bbqiu.middleware;

import java.util.List;

/**
 * @author: nixgnehc nixgnehc@163.com
 * @date: 19-1-16
 * @time: 下午4:58
 * @Description: TODO..
 */

public abstract class Local {

    private Integer maxTask;

    private List<String> locaTask;

    private List<String> coordinatorTask;

    private Integer peerNum;


    public Integer getPeerNum() {
        return peerNum;
    }

    public void setPeerNum(Integer peerNum) {
        this.peerNum = peerNum;
        calculationLocalMaxTask();
    }

    public Integer getMaxTask() {
        return maxTask;
    }


    public List<String> getLocaTask() {
        return locaTask;
    }


    public List<String> getCoordinatorTask() {
        return coordinatorTask;
    }

    public void setCoordinatorTask(List<String> coordinatorTask) {
        this.coordinatorTask = coordinatorTask;
        calculationLocalMaxTask();
    }

    /**
     * 计算本地最大任务
     */
    private void calculationLocalMaxTask(){
        this.maxTask = coordinatorTask.size() / peerNum;
        if (0 != coordinatorTask.size() % peerNum) {
            this.maxTask++;
        }
    }

}
