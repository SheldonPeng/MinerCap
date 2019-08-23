package org.qgstudio.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.qgstudio.model.Feedback;
import org.qgstudio.model.Message;
import org.qgstudio.utils.SocketClientPool;
import org.qgstudio.utils.SocketMsgAnalyUtils;
import org.qgstudio.utils.WebsocketClientPool;
import org.qgstudio.service.MessageService;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @Description: 用于消息的处理服务层
 * @Param:
 * @return:
 * @Author: SheldonPeng
 * @Date: 2019-07-25
 */
@Service("messageService")
public class MessageServiceImpl implements MessageService {


    // websocket(安卓)用户连接池
    private WebsocketClientPool webSocketClientPool = WebsocketClientPool.getClientPool();
    // socket(嵌入式设备)客户端连接池
    private SocketClientPool socketClientPool = SocketClientPool.getClientPool();
    // 格式化工具
    private Charset charset = Charset.forName("UTF-8");
    // json格式化工具
    ObjectMapper objectMapper = new ObjectMapper();



    @Override
    public void sendMsgToSocket(String message) {

        List<SocketChannel> channelList = socketClientPool.getAllClient();
        for (SocketChannel socketChannel:
            channelList ) {

            try {
                socketChannel.write(charset.encode(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void sendMsgToWebSocket(String message) {

        try {
            List<WebSocketSession> channelList = webSocketClientPool.getAllClient();
            for (WebSocketSession webSocketSession :
                    channelList) {

                synchronized (webSocketSession){

                    webSocketSession.sendMessage(new TextMessage(message));

                }
            }
        } catch (IOException e) {
            System.out.println("发送消息出现异常");
        }

    }

    @Override
    public void sendDataMsgToWebSocket(Message message) throws JsonProcessingException {

        sendMsgToWebSocket(objectMapper.writeValueAsString(message));
    }

    @Override
    public void sendPingMsgToWebSocket(String message) {

        try {
            Message msg = new Message();
            msg.setStatus(true);
            msg.setData("");
            msg.setAddress(message);
            sendMsgToWebSocket(objectMapper.writeValueAsString(msg));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendModelExceToWebSocket(String model) {

        try {
            Message msg = new Message();
            msg.setStatus(false);
            msg.setAddress(model);
            msg.setData("");
            sendMsgToWebSocket(objectMapper.writeValueAsString(msg));

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendFeedBackMsgToWebSocket(Feedback feedBack) throws JsonProcessingException {

        System.out.println(feedBack);
        sendMsgToWebSocket(objectMapper.writeValueAsString(feedBack));
    }
}
