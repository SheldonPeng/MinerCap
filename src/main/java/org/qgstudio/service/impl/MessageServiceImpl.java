package org.qgstudio.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.qgstudio.model.Message;
import org.qgstudio.utils.SocketClientPool;
import org.qgstudio.utils.SocketMsgAnalyUtils;
import org.qgstudio.utils.WebsocketClientPool;
import org.qgstudio.service.MessageService;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.websocket.Session;
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

        List<Session> channelList = webSocketClientPool.getAllClient();
        for (Session webSocketSession :
                channelList) {
            webSocketSession.getAsyncRemote().sendText(message);
        }

    }

    @Override
    public void analyMessage(String message) {

        try {
        Message msg = SocketMsgAnalyUtils.getMessage(message);
        ObjectMapper objectMapper = new ObjectMapper();
            sendMsgToWebSocket(objectMapper.writeValueAsString(msg));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


}
