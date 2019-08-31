package org.qgstudio.controller.ContextListener;

import org.qgstudio.netty.config.WebsocketConfig;
import org.qgstudio.service.impl.SocketThread;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;


/**
 * @Description: 用于与嵌入式设备通信的socket服务端的启动
 * @Param:
 * @return:
 * @Author: SheldonPeng
 * @Date: 2019-07-25
 */
@WebListener
public class ContextListener implements ServletContextListener {


    private static SocketThread socketThread = null;

    private static WebsocketConfig websocketConfig = null;

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        if ( socketThread == null){

            socketThread = new SocketThread();
            socketThread.start();
        }
        if ( websocketConfig == null ){

            websocketConfig = new WebsocketConfig();
            websocketConfig.start();
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

        if( socketThread != null || socketThread.isAlive()){

            socketThread.interrupt();
        }
        if ( websocketConfig != null || websocketConfig.isAlive()){

            websocketConfig.interrupt();
        }
    }
}
