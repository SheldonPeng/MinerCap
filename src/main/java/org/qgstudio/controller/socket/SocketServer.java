package org.qgstudio.controller.socket;

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
public class SocketServer implements ServletContextListener {


    private SocketThread socketThread = null;


    @Override
    public void contextInitialized(ServletContextEvent sce) {

        if ( socketThread == null){

            socketThread = new SocketThread();
            socketThread.start();
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

        if( socketThread != null || socketThread.isAlive()){

            socketThread.interrupt();
        }
    }
}
