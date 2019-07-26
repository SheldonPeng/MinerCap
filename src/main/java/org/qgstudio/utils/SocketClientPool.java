package org.qgstudio.utils;


import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Description: 嵌入式设备socket端用户池
 * @Param:
 * @return:
 * @Author: SheldonPeng
 * @Date: 2019-07-25
 */
public class SocketClientPool {

    // 用于保存socket客户端的集合
    private static final CopyOnWriteArraySet<SocketChannel> clients;

    // 建立单例对象
    private static final SocketClientPool clientPool = new SocketClientPool();
    private SocketClientPool(){};
    public static SocketClientPool getClientPool(){
        return clientPool;
    }
    static {
        clients = new CopyOnWriteArraySet<>();
    }

    /**
     * @Description: 新用户连接的时候
     * @Param: [client]
     * @return: void
     * @Author: SheldonPeng
     * @Date: 2019-07-25
     */
    public void addClient(SocketChannel client){

        // 判断是否存在于集合中
        if ( ! clients.contains(client)){

            clients.add(client);
        }
    }

    /**
     * @Description: 客户端连接关闭
     * @Param: [client]
     * @return: void
     * @Author: SheldonPeng
     * @Date: 2019-07-25
     */
    public void exitClient(SocketChannel client){

        try {
            if( clients.contains(client)){

                clients.remove(client);
                client.shutdownInput();
                client.shutdownOutput();
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * @Description: 获取socket客户端的数量
     * @Param: []
     * @return: int
     * @Author: SheldonPeng
     * @Date: 2019-07-25
     */
    public int getClientCount(){

        return clients.size();
    }


    public List<SocketChannel> getAllClient(){

        List<SocketChannel> clientList = new ArrayList<>();

        for (SocketChannel client :
                clients) {
            // 判断客户端是否为空
            if (client != null) {
                clientList.add(client);
            }
        }
        return clientList;
    }

}
