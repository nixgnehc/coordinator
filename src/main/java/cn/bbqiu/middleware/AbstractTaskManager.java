package cn.bbqiu.middleware;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author: nixgnehc nixgnehc@163.com
 * @date: 19-1-16
 * @time: 下午6:58
 * @Description: TODO..
 */

public abstract class AbstractTaskManager implements TaskManager {

    Gson gson = new Gson();
    Logger logger = LoggerFactory.getLogger(AbstractTaskManager.class);
    @Override
    public void revise(List<String> currentTask, List<String> coordinatorTask) {

        Map<String, TaskSurvivalEntity> survivalMap = Maps.newHashMap();
        if (null != coordinatorTask) {
            logger.debug("coordinatorTask:", coordinatorTask.size());
            coordinatorTask.stream().forEach(x -> {
                TaskSurvivalEntity entity = new TaskSurvivalEntity();
                entity.setInCoordinator(true);
                entity.setInCurrent(false);
                entity.setTask(x);
                survivalMap.put(x, entity);
            });
        }
        if (null != currentTask) {
            logger.debug("currentTask:", currentTask.size());
            currentTask.stream().forEach(x -> {
                TaskSurvivalEntity entity = survivalMap.get(x);
                if (null != entity) {
                    survivalMap.remove(x, entity);
                } else {
                    entity = new TaskSurvivalEntity();

                    entity.setTask(x);
                    entity.setInCoordinator(false);
                    entity.setInCurrent(true);

                    survivalMap.put(x, entity);
                }
            });
        }

        survivalMap.entrySet().stream().forEach(x -> {
            TaskSurvivalEntity entity = x.getValue();
            if (entity.getInCurrent() && !entity.getInCoordinator()) {

                createTask(entity.getTask());
            } else if (entity.getInCoordinator() && !entity.getInCurrent()) {
                deleteTask(entity.getTask());
            }
        });
    }


    @Override
    public void clean(List<String> tasks) {
        tasks.stream().forEach(x->{
            deleteTask(x);
        });
    }
}
