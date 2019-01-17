package cn.bbqiu.middleware.test.utils;

import cn.bbqiu.middleware.CoordinatorTaskLoading;
import org.testng.collections.Lists;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author: chengxin chengxin@zbj.com
 * @date: 19-1-17
 * @time: 下午5:11
 * @Description: TODO..
 */

public class CoordinatorTaskLoadingUtil {

    static SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHH");

    public static CoordinatorTaskLoading coordinatorTaskLoading(){
        return new CoordinatorTaskLoading() {
            @Override
            public List<String> loading() {
                Long taskNum = System.currentTimeMillis() % 20 + 2;
                List<String> tasks = Lists.newArrayList();
                Date date = new Date();
                for (long i = 0 ; i <= taskNum; i++){
                    tasks.add(String.format("%s%dt",formatter.format(date), i));
                }
                return tasks;
            }
        };
    }
}
