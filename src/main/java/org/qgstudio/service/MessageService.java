package org.qgstudio.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.qgstudio.model.Feedback;
import org.qgstudio.model.Message;

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
     * @Description: 发送data类型的数据给移动端
     * @Param: [message]
     * @return: void
     * @Author: SheldonPeng
     * @Date: 2019-07-28
     */
    void sendDataMsgToWebSocket(Message message) throws JsonProcessingException;

    /**
     * @Description: 发送小模块的心跳信息给手机端
     * @Param: [message]
     * @return: void
     * @Author: SheldonPeng
     * @Date: 2019-07-26
     */
    void sendPingMsgToWebSocket(String message);

    /**
     * @Description: 发送小模块异常的消息给移动端
     * @Param: []
     * @return: void
     * @Author: SheldonPeng
     * @Date: 2019-07-26
     */
    void sendModelExceToWebSocket(String model);

    /**
     * @Description: 发送反馈类型的消息给移动端
     * @Param: [feekBack]
     * @return: void
     * @Author: SheldonPeng
     * @Date: 2019-07-28
     */
    void sendFeedBackMsgToWebSocket(Feedback feedBack) throws JsonProcessingException;
}
