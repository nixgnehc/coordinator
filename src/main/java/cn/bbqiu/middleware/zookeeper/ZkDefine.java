package cn.bbqiu.middleware.zookeeper;

/**
* @author: nixgnehc nixgnehc@163.com
 * @date: 19-1-16
 * @time: 下午4:10
 * @Description: TODO..
 */

public class ZkDefine {

    private String basePath;

    private String lockBasePath;

    private String taskBasePath;

    private String peersBasePath;

    private String zkHosts;


    public ZkDefine(String zkHosts, String basePath) {
        this.zkHosts = zkHosts;
        this.basePath = basePath;
        this.lockBasePath = String.format("%s/%s", basePath, "lock");
        this.taskBasePath = String.format("%s/%s", basePath, "task");
        this.peersBasePath = String.format("%s/%s", basePath, "peers");
    }


    public String getZkHosts() {
        return zkHosts;
    }

    public String getBasePath() {
        return basePath;
    }

    public String getLockBasePath() {
        return lockBasePath;
    }

    public String getTaskBasePath() {
        return taskBasePath;
    }

    public String getPeersBasePath() {
        return peersBasePath;
    }
}
