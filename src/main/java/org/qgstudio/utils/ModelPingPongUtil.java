package org.qgstudio.utils;

import org.qgstudio.service.MessageService;
import org.qgstudio.service.impl.MessageServiceImpl;

import java.util.Map;
import java.util.TimerTask;

/**
 * @Description: 小模块的状态监测
 * @Param:
 * @return:
 * @Author: SheldonPeng
 * @Date: 2019-07-26
 */
public class ModelPingPongUtil extends TimerTask {



    private MessageService messageService = new MessageServiceImpl();

    // 超时时间
    private final int TIME_OUT = 1000*8;

    // 小模块的活跃时间记录
    private static volatile Map<String,Long>modelTimeMileMap;


    public ModelPingPongUtil(Map<String,Long>modelTimeMileMap){

        this.modelTimeMileMap = modelTimeMileMap;
    }

    @Override
    public void run() {

        for (String model:
             modelTimeMileMap.keySet()) {
            // 检测小模块的活跃时间，超时则下线
            if( System.currentTimeMillis() - modelTimeMileMap.get(model) > TIME_OUT){

                System.out.println(model + "小模块下线");
                modelTimeMileMap.remove(model);
                messageService.sendModelExceToWebSocket(model);
            }
        }
    }
}
