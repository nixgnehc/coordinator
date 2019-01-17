package cn.bbqiu.middleware;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author: nixgnehc nixgnehc@163.com
 * @date: 19-1-17
 * @time: 上午12:09
 * @Description: TODO..
 */

public abstract class AbstractReBalance implements ReBalance {

    public Local local;

    @Override
    public void coordinatorTaskChange(NotfiyCallBack callback, Local local) {
        List<String> canalTask = Lists.newArrayList();
            local.getLocaTask().stream().forEach(x -> {
                if (!local.getCoordinatorTask().contains(x)) {
                    callback.call(x, Type.Lose);
                    canalTask.add(x);
                }
            });
        canalTask.stream().forEach(x -> {
            local.getLocaTask().remove(x);
        });
    }

    @Override
    public void localLose(NotfiyCallBack callback, Local local) {
        Integer needLoseTaskNumber = local.getLocaTask().size() - local.getMaxTask();
        if (needLoseTaskNumber > 0) {
            for (int i = 0; i < needLoseTaskNumber; i++) {
                String task = local.getLocaTask().get(i);
                if (lose(task)) {
                    local.getLocaTask().remove(i);
                    callback.call(task, Type.Lose);
                }
            }
        }
    }

    @Override
    public void localScramble(NotfiyCallBack callback, Local local) {
        local.getCoordinatorTask().stream().forEach(x -> {
            Integer localTaskNum = (null == local.getLocaTask()? 0 : local.getLocaTask().size());
            if (localTaskNum >= local.getMaxTask()) {
                return;
            }
            if (scramble(x)) {
                callback.call(x, Type.Obtain);
                local.getLocaTask().add(x);
            }
        });
    }

}
