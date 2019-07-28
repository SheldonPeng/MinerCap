package org.qgstudio.utils;

import org.qgstudio.service.MessageService;
import org.qgstudio.service.impl.MessageServiceImpl;

import java.nio.channels.SocketChannel;
import java.security.PrivateKey;
import java.util.Map;
import java.util.TimerTask;

/**
 * @Description: 用于检测socket是否处于活跃状态
 * @Param:
 * @return:
 * @Author: SheldonPeng
 * @Date: 2019-07-26
 */
public class CapPingPongUtil extends TimerTask {

    // 获取socket(嵌入式)端的用户连接池
    private SocketClientPool socketClientPool = SocketClientPool.getClientPool();


    private MessageService messageService = new MessageServiceImpl();

    // 超时时间
    private final int TIME_OUT = 1000*8;

    // socket客户端上次活跃时间记录
    private static  volatile Map<SocketChannel,Long>timeMileMap;

    // 小模块的活跃状态集合
    private static volatile Map<String,Long>modelTimeMileMap;

    public CapPingPongUtil(Map<SocketChannel,Long>timeMileMap , Map<String,Long>modelTimeMileMap){

        this.timeMileMap = timeMileMap;
        this.modelTimeMileMap = modelTimeMileMap;
    }

    @Override
    public void run() {

        for (SocketChannel channel :
                timeMileMap.keySet()) {
            if (System.currentTimeMillis() - timeMileMap.get(channel) > TIME_OUT) {

                System.out.println("嵌入式设备下线");
                messageService.sendMsgToWebSocket("嵌入式设备下线");
                socketClientPool.exitClient(channel);
                timeMileMap.remove(channel);
                modelTimeMileMap.clear();
            }
        }
    }
}
