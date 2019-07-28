package org.qgstudio.utils;

import org.qgstudio.model.Feedback;
import org.qgstudio.model.Message;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        dataMap.put("quality","空气质量状态异常\n");
        dataMap.put("gas","天然气浓度异常\n");
        dataMap.put("co","一氧化碳浓度异常\n");
        dataMap.put("liquefied","液化气浓度异常\n");
        dataMap.put("fell","摔倒\n");
        dataMap.put("got","我收到了\n");
        dataMap.put("help","求救\n");

    }

    
    /**
     * @Description: 解析嵌入式设备消息 model格式为 model@XXX%  locate格式为 locate@XXX#XXX%
     * rescue格式为 rescue@XXX%  data格式为 data@XXXX#XXX%
     * @Param: [message]
     * @return: org.qgstudio.model.Message
     * @Author: SheldonPeng
     * @Date: 2019-07-26
     */
    public static Map<String,List> analyAddress(String message){



        Map<String,List> msgMap = new HashMap<>();

        msgMap.put("model",analyModelMsg(message));
        msgMap.put("locate",analyLocateMsg(message));
        msgMap.put("rescue",ananyRescueMsg(message));
        msgMap.put("data",analyDataMsg(message));

        return msgMap;
    }

    /**
     * @Description: 分解小模块类型的消息  model格式为 model@XXX%
     * @Param: [message]
     * @return: java.util.List<java.lang.String>
     * @Author: SheldonPeng
     * @Date: 2019-07-28
     */
    public static List<String> analyModelMsg(String message){


        List<String> modelList = new ArrayList<>();
        // 小模块匹配的正则
        String modelRgex = "model@(.*?)%";
        Pattern modelPattern = Pattern.compile(modelRgex);
        Matcher modelMatcher = modelPattern.matcher(message);

        while ( modelMatcher.find()){
            int i = 1;
            String oldAddress = modelMatcher.group(i);
            String newAddress = oldAddress.substring(0,2) + "0" + oldAddress.substring(2);
            modelList.add(newAddress);
            i++;
        }
        return modelList;
    }

    /**
     * @Description: 分解位置类型的消息  locate格式为 locate@XXX#XXX%
     * @Param: [message]
     * @return: java.util.List<org.qgstudio.model.Feedback>
     * @Author: SheldonPeng
     * @Date: 2019-07-28
     */
    public static List<Feedback> analyLocateMsg(String message){


        List<Feedback> locateList = new ArrayList<>();
        // locate匹配的正则
        String locateRgex = "locate@(.*?)%";
        Pattern locatePattern = Pattern.compile(locateRgex);
        Matcher locateMatcher = locatePattern.matcher(message);

        while ( locateMatcher.find()){
            int i = 1;
            String locateMsg = locateMatcher.group(i);
            Feedback feedback = new Feedback();
            feedback.setLocate(true);
            feedback.setRescue(false);
            feedback.setMessage(locateMsg.split("#")[0]);
            feedback.setAddress(locateMsg.split("#")[1].substring(0,2) + "0" +
                    locateMsg.split("#")[1].substring(2));
            locateList.add(feedback);
        }
        return locateList;
    }

    /**
     * @Description: 分解反馈类型的消息  rescue格式为 rescue@XXX%
     * @Param: [message]
     * @return: java.util.List<org.qgstudio.model.Feedback>
     * @Author: SheldonPeng
     * @Date: 2019-07-28
     */
    public static List<Feedback> ananyRescueMsg(String message){


        List<Feedback> rescueList = new ArrayList<>();
        // rescue匹配的正则
        String rescueRgex = "rescue@(.*?)%";

        Pattern rescuePattern = Pattern.compile(rescueRgex);
        Matcher rescueMatcher = rescuePattern.matcher(message);

        while ( rescueMatcher.find()){
            int i = 1;
            Feedback feedback = new Feedback();
            feedback.setRescue(true);
            feedback.setLocate(false);
            feedback.setMessage("我已收到反馈!\n");
            String oldAddress = rescueMatcher.group(i);
            String newAddress = oldAddress.substring(0,2) + "0" + oldAddress.substring(2);
            feedback.setAddress(newAddress);
            rescueList.add(feedback);
            i++;
        }
        return rescueList;
    }

    /**
     * @Description: 分解data类型的数据  data格式为 data@XXXX#XXX%
     * @Param: [message]
     * @return: java.util.List<org.qgstudio.model.Message>
     * @Author: SheldonPeng
     * @Date: 2019-07-28
     */
    public static List<Message> analyDataMsg(String message){

        List<Message> dataList = new ArrayList<>();
        // rescue匹配的正则
        String dataRgex = "data@(.*?)%";


        Pattern dataPattern = Pattern.compile(dataRgex);
        Matcher dataMatcher = dataPattern.matcher(message);

        while ( dataMatcher.find()){
            int i = 1;

            String dataMsg = dataMatcher.group(i);
            Message newMsg = new Message();
            newMsg.setStatus(true);
            newMsg.setData(dataMap.get(dataMsg.split("#")[0]));
            newMsg.setAddress(dataMsg.split("#")[1].substring(0,2) + "0" +
                    dataMsg.split("#")[1].substring(2));
            dataList.add(newMsg);
            i++;
        }
        return dataList;
    }

}
