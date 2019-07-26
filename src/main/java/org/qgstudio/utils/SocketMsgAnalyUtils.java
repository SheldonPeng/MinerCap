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

    public static Message getMessage(String message){

        Message msg  =  new Message();
        // 当嵌入式设备发送的为位置和数据的信息类型
        try {
            if( ! (message.contains("#") || message.contains("@"))){

                msg.setStatus(false);
                return msg;
            }
            if ( message.contains("address") && message.contains("data") ){

                // 消息类型为  address:XXX@data:XXXX#
                String[] content = message.split("@");

                // address字符不全，需要由后台进行补全
                msg.setAddress(content[0].substring(8,10) + "0" + content[0].substring(10));

                msg.setData(dataMap.get(content[1].substring(5,9)));

                if( msg.getData() == null){
                    msg.setStatus(false);
                }else {

                    msg.setStatus(true);
                }
            } else if ( message.contains("address") && message.contains("location")){

                //
            }else {

                msg.setStatus(false);
            }
        } catch (Exception e) {
            msg.setStatus(false);
        }
        return msg;
    }
}
