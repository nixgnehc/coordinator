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

    public void setLocaTask(List<String> locaTask) {
        this.locaTask = locaTask;
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
     * 这是一段富含哲学逻辑的代码
     */
    private void calculationLocalMaxTask() {
        int size = (null == coordinatorTask ? 0 : coordinatorTask.size());
        peerNum = ((null == peerNum || peerNum == 0) ? 1 : peerNum);
        this.maxTask = size / peerNum;
        if (0 != size % peerNum) {
            this.maxTask++;
        }
    }

}
