package org.qgstudio.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.WebSocketSession;

import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Description: websocket客户连接池
 * @Param:
 * @return:
 * @Author: SheldonPeng
 * @Date: 2019-07-25
 */
public class WebsocketClientPool {

    // 用于记录安卓客户端的连接session
    private static final CopyOnWriteArraySet<WebSocketSession> clients;

    // 建立单例对象
    private static final WebsocketClientPool clientPool = new WebsocketClientPool();
    private WebsocketClientPool(){};
    public static WebsocketClientPool getClientPool(){
        return clientPool;
    }

    // 初始化集合
    static {

        clients = new CopyOnWriteArraySet<WebSocketSession>();
    }

    /**
     * @Description: 当新用户连接时，添加至集合中
     * @Param: []
     * @return: void
     * @Author: SheldonPeng
     * @Date: 2019-07-25
     */
    public void addClient(WebSocketSession session) {

        // 检测是否已有用户在线
        if (!clients.contains(session)) {
            clients.add(session);
        }
    }


    /**
     * @Description: 用户退出连接，移出集合
     * @Param: [session]
     * @return: void
     * @Author: SheldonPeng
     * @Date: 2019-07-25
     */

    public void exitClient(WebSocketSession session) throws IOException {

        // 检测用户是否在集合中
        if (clients.contains(session)) {

            // 检测用户是否打开连接，主动关闭连接
            if( session.isOpen()){
                session.close();
            }
            clients.remove(session);
            System.out.println("关闭了一个链接");
        }
    }


    /**
     * @Description: 获取所有在线用户的seesion
     * @Param: [session]
     * @return: org.springframework.web.socket.WebSocketSession
     * @Author: SheldonPeng
     * @Date: 2019-07-25
     */
    public List<WebSocketSession> getAllClient(){

        List<WebSocketSession> sessions = new ArrayList<>();

        for (WebSocketSession session:
             clients) {
            // 判断客户端是否为空客户端
            if( session != null){

                sessions.add(session);
            }
        }

        return sessions;
    }


    /**
     * @Description: 返回当前安卓客户端在线数目
     * @Param: []
     * @return: int
     * @Author: SheldonPeng
     * @Date: 2019-07-25
     */
    public int getClientCount(){

        return clients.size();
    }


}
