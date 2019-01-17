package cn.bbqiu.middleware;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * @author: nixgnehc nixgnehc@163.com
 * @date: 19-1-16
 * @time: 下午6:58
 * @Description: TODO..
 */

public abstract class AbstractTaskManager implements TaskManager {

    @Override
    public void revise(List<String> currentTask, List<String> coordinatorTask) {

        Map<String, TaskSurvivalEntity> map = Maps.newHashMap();
        if (null != coordinatorTask) {
            coordinatorTask.stream().forEach(x -> {
                TaskSurvivalEntity entity = new TaskSurvivalEntity();
                entity.setInCoordinator(true);
                entity.setInCurrent(false);
                entity.setTask(x);
                map.put(x, entity);
            });
        }
        if (null != currentTask) {
            currentTask.stream().forEach(x -> {
                TaskSurvivalEntity entity = map.get(x);
                if (null != entity) {
                    map.remove(entity);
                } else {
                    entity = new TaskSurvivalEntity();

                    entity.setTask(x);
                    entity.setInCoordinator(false);
                    entity.setInCurrent(true);

                    map.put(x, entity);
                }
            });
        }

        map.entrySet().stream().forEach(x -> {
            TaskSurvivalEntity entity = map.get(x);
            if (entity.getInCurrent() && !entity.getInCoordinator()) {

                createTask(entity.getTask());
            } else if (entity.getInCoordinator() && !entity.getInCurrent()) {
                deleteTask(entity.getTask());
            }
        });
    }


}
