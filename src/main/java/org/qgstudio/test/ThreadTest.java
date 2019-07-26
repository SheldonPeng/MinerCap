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

        String msg = "address:2A6@data:ABCD#";
        System.out.println(SocketMsgAnalyUtils.getMessage(msg));
    }
}
