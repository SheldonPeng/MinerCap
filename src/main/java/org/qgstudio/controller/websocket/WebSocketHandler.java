package org.qgstudio.controller.websocket;

import org.qgstudio.utils.SocketClientPool;
import org.qgstudio.service.MessageService;
import org.qgstudio.service.impl.MessageServiceImpl;
import org.qgstudio.utils.WebsocketClientPool;


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
@ServerEndpoint(value = "/websocket")
public class WebSocketHandler {


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
    @OnOpen
    public void onOpen(Session session , EndpointConfig endpointConfig){

        System.out.println(session.getRequestURI() + "已连接");

        websocketClientPool.addClient(session);

        if ( socketClientPool.getClientCount() == 0){
            session.getAsyncRemote().sendText(ZeroCapConnection);
        } else {

            session.getAsyncRemote().sendText("当前有" + socketClientPool.getClientCount() +"个设备在线!");
        }


    }

    /**
     * @Description: 接收到来自安卓客户端的消息
     * @Param: [session, message]
     * @return: void
     * @Author: SheldonPeng
     * @Date: 2019-07-25
     */
    @OnMessage
    public void onMessage(String message, Session session){

        System.out.println(session.getRequestURI() + message.toString());
        session.getAsyncRemote().sendText("收到信息为" + message);
        messageService.sendMsgToSocket(message);

    }


    /**
     * @Description: 处理与安卓客户端连接断开
     * @Param: [session, status]
     * @return: void
     * @Author: SheldonPeng
     * @Date: 2019-07-25
     */
    @OnClose
    public void onClose(Session session){

        try {
            websocketClientPool.exitClient(session);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @OnError
    public void onError(Session session, Throwable thr) throws IOException {
        System.out.println("error");
        websocketClientPool.exitClient(session);
    }

}
