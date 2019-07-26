package org.qgstudio.service;

public interface MessageService {


    /**
     * @Description: 由websocket(安卓客户端)发送消息给socket端(嵌入式端)
     * @Param: [message]
     * @return: void
     * @Author: SheldonPeng
     * @Date: 2019-07-25
     */
    void sendMsgToSocket(String message);
    
    
    /**
     * @Description: 由socket(嵌入式端)发送消息给websocket(安卓客户端)
     * @Param: [message]
     * @return: void
     * @Author: SheldonPeng
     * @Date: 2019-07-25
     */
    void sendMsgToWebSocket(String message);

    /**
     * @Description: 解析socket(嵌入式端)的信息
     * @Param: [message]
     * @return: void
     * @Author: SheldonPeng
     * @Date: 2019-07-25
     */
    void analyMessage(String message);
}
