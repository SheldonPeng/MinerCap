package org.qgstudio.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.qgstudio.model.Feedback;
import org.qgstudio.utils.SocketClientPool;
import org.qgstudio.service.MessageService;
import org.qgstudio.service.impl.MessageServiceImpl;
import org.qgstudio.utils.WebsocketClientPool;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;


/**
 * @Description: websocket的相关处理类
 * @Param:
 * @return:
 * @Author: SheldonPeng
 * @Date: 2019-07-25
 */
@Service
public class WebSocketHandler extends TextWebSocketHandler {


    private WebsocketClientPool websocketClientPool = WebsocketClientPool.getClientPool();

    private SocketClientPool socketClientPool = SocketClientPool.getClientPool();

    // 无帽子在线的字符串常量
    private final String ZeroCapConnection = "当前没有嵌入式设备在线!";

    private MessageService messageService = new MessageServiceImpl();

    /**
     * @Description: 当新连接建立完成之后，储存连接用户的session
     * @Param: [session]
     * @return: void
     * @Author: SheldonPeng
     * @Date: 2019-07-25
     */

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {


        System.out.println(session.getRemoteAddress().getAddress().getHostName() + "已连接");

        websocketClientPool.addClient(session);

        if ( socketClientPool.getClientCount() == 0){
            session.sendMessage(new TextMessage(ZeroCapConnection));
        } else {

            session.sendMessage(new TextMessage("当前有" + socketClientPool.getClientCount() +"个设备在线!"));
        }


    }
    /**
     * @Description: 接收到来自安卓客户端的消息
     * @Param: [session, message]
     * @return: void
     * @Author: SheldonPeng
     * @Date: 2019-07-25
     */

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {


        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(session.getRemoteAddress().getAddress().getHostName() + message.toString());

        Feedback feedback = (Feedback) objectMapper.readValue(message.getPayload(),Feedback.class);
        String newMsg = "";

        if (feedback != null){

             // 请求定位的内容
            if ( feedback.isLocate()){

                newMsg = "locate@" + feedback.getAddress() + "%";

                // 请求反馈的内容
            } else if ( feedback.isRescue()){

                newMsg = "rescue@" + feedback.getAddress() + "%";
            }
        }
        System.out.println("向嵌入式端发送了" + newMsg);
        messageService.sendMsgToSocket(newMsg);

    }

    /**
     * @Description: 处理与安卓客户端连接断开
     * @Param: [session, status]
     * @return: void
     * @Author: SheldonPeng
     * @Date: 2019-07-25
     */

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        try {
            websocketClientPool.exitClient(session);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

        System.out.println("error");
        websocketClientPool.exitClient(session);
    }

}
