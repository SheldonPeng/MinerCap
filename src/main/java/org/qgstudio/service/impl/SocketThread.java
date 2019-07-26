package org.qgstudio.service.impl;

import org.qgstudio.utils.CapPingPongUtil;
import org.qgstudio.utils.SocketMsgAnalyUtils;
import org.qgstudio.utils.WebsocketClientPool;
import org.qgstudio.service.MessageService;
import org.qgstudio.utils.SocketClientPool;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 开辟一条新的线程用于与嵌入式端进行NIO非阻塞通信
 * @Param:
 * @return:
 * @Author: SheldonPeng
 * @Date: 2019-07-25
 */

public class SocketThread extends Thread{

    // 端口号
    private int port = 8888;
    // 选择器，用于注册chanel
    private Selector selector = null;
    // 指定发送的字符类型
    private Charset charset = Charset.forName("UTF-8");
    // 检测时间为5S
    private final int CHECK_TIME = 1000*5;
    // 用于存储websocket(移动端)的客户端集合
    private WebsocketClientPool websocketClientPool = WebsocketClientPool.getClientPool();

    // 用于存储socket（嵌入式端)客户端集合
    private SocketClientPool socketClientPool = SocketClientPool.getClientPool();

    // 用于记录每一个客户端上一次与服务器通信的时间
    private static final  Map<SocketChannel,Long> capTimeMileMap = new ConcurrentHashMap<>();

    // 用于记录每个客户端的小模块的状态
    private static final Map<String,Long> modelTimeMileMap = new ConcurrentHashMap<>();

    // 退出字符常量
    private final String exitClientMsg = "嵌入式设备下线";

    // 帽子连接的字符串常量
    private final String startConnection = "嵌入式设备连接";

    private MessageService messageService = new MessageServiceImpl();

    public SocketThread() {

        try {
            ServerSocketChannel socketChannel = ServerSocketChannel.open();
            socketChannel.bind(new InetSocketAddress("localhost",this.port));
            socketChannel.configureBlocking(false);

            selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("服务端已启动,端口为： " + this.port);

            Timer timer = new Timer();
            timer.schedule(new CapPingPongUtil(capTimeMileMap,modelTimeMileMap),0,CHECK_TIME);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        while (true){

            try {
                int wait = selector.select();
                if( wait == 0 ) {
                    continue;
                }
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()){

                    SelectionKey key = iterator.next();
                    iterator.remove();
                    process(key);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * @Description: 用于处理客户端的消息
     * @Param: [key]
     * @return: void
     * @Author: SheldonPeng
     * @Date: 2019-07-25
     */
    private void process(SelectionKey key) throws IOException {

        //  接收到连接的请求
        if( key.isAcceptable()){

            ServerSocketChannel server = (ServerSocketChannel)key.channel();
            SocketChannel client = server.accept();

            client.configureBlocking(false);
            client.register(selector,SelectionKey.OP_READ);

            key.interestOps(SelectionKey.OP_ACCEPT);
            System.out.println("新的连接 ：" + client.getRemoteAddress());
            socketClientPool.addClient(client);
            messageService.sendMsgToWebSocket(startConnection);

            // 添加对应socket客户端的活跃时间
            capTimeMileMap.put(client,System.currentTimeMillis());

            // 接收到来自客户端的信息
        } if (key.isReadable()) {

            SocketChannel client = (SocketChannel) key.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            StringBuilder content = new StringBuilder();

            // 更新对应socket客户端的活跃时间
            capTimeMileMap.put(client, System.currentTimeMillis());

            try {
                while (client.read(byteBuffer) > 0) {
                    byteBuffer.flip();
                    content.append(charset.decode(byteBuffer));
                }
                key.interestOps(SelectionKey.OP_READ);
                System.out.println(content.toString());

                //  客户端主动断开或者被迫断开的时候
                if ("exit".equals(content.toString()) || "".equals(content.toString())) {
                    System.out.println(client.getRemoteAddress() + "已断开连接");
                    key.cancel();
                    socketClientPool.exitClient(client);
                    capTimeMileMap.remove(client);
                    modelTimeMileMap.clear();
                    return;
                }
                // socket(嵌入式客户端)主网络模块的心跳字段
                if ("ping".equals(content.toString())) {

                    client.write(charset.encode("pong"));
                    return;
                }
                // socket(嵌入式客户端)小模块心跳字段
                if (content.toString().contains("ping@")) {

                    // 更新小模块的活跃状态
                    modelTimeMileMap.put(SocketMsgAnalyUtils.analyAddress(content.toString()),System.currentTimeMillis());

                    // 发送小模块的更新状态给手机端
                    messageService.sendPingMsgToWebSocket(content.toString());
                    return;
                }

                // 解析嵌入式端发送的其他类消息，并发送给移动端
                messageService.analyMessage(content.toString());

            } catch (IOException e) {
                key.cancel();
                if (key.channel() != null) {
                    key.channel().close();
                }
            }
        }

    }
}
