package org.qgstudio.test;

import org.qgstudio.service.impl.SocketThread;
import org.qgstudio.utils.SocketMsgAnalyUtils;

/**
 * @Description: $
 * @Param: $
 * @return: $
 * @Author: SheledonPeng
 * @Date: $
 */
public class ThreadTest {


    public static void main(String[] args) {

        String msg = "ping@2A6";
        System.out.println(SocketMsgAnalyUtils.analyAddress(msg));
    }
}
