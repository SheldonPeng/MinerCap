package org.qgstudio.utils;

import org.qgstudio.model.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 用于解析嵌入式客户端的信息的工具
 * @Param:
 * @return:
 * @Author: SheldonPeng
 * @Date: 2019-07-25
 */

public class SocketMsgAnalyUtils {

    // 普通传输数据的常量map集合
    private static final Map<String,String> dataMap = new HashMap<>();

    static {

        dataMap.put("quality","空气质量");
        dataMap.put("gas","天然气");
        dataMap.put("co","一氧化碳");
        dataMap.put("liquefied","液化气");
        dataMap.put("fell","摔倒");
        dataMap.put("got","我收到了");
        dataMap.put("help","求救");

    }

    /**
     * @Description: 解析嵌入式设备中的正常信息
     * @Param: [message]
     * @return: org.qgstudio.model.Message
     * @Author: SheldonPeng
     * @Date: 2019-07-26
     */
    public static Message getMessage(String message){

        Message msg  =  new Message();
        // 当嵌入式设备发送的为位置和数据的信息类型
        try {
            if( ! (message.contains("#") || message.contains("@"))){

                msg = null;
            }
            if ( message.contains("address") && message.contains("data") ){

                // 消息类型为  address:XXX@data:XXXX#
                String[] content = message.split("@");

                // address字符不全，需要由后台进行补全
                msg.setAddress(content[0].substring(8,10) + "0" + content[0].substring(10));

                // 获取嵌入式端设备传来的data并解析
                msg.setData(dataMap.get(content[1].substring(5,9)));

                // 如果发送的data没有在集合中，即data异常
                if( msg.getData() == null){

                    msg = null;
                }
            } else if ( message.contains("address") && message.contains("location")){

                //
            }else {

                msg = null;
            }
        } catch (Exception e) {

            msg = null;
        }
        return msg;
    }

    /**
     * @Description: 解析嵌入式设备(心跳包)发送的地址信息 格式为 ping@XXXX
     * @Param: [message]
     * @return: org.qgstudio.model.Message
     * @Author: SheldonPeng
     * @Date: 2019-07-26
     */
    public static String analyAddress(String message){

        // 解析前的地址信息
        String oldAddress = message.split("@")[1];
        // 返回解析后的地址信息
        return (oldAddress.substring(0,2) + "0" + oldAddress.substring(2));

    }
}
