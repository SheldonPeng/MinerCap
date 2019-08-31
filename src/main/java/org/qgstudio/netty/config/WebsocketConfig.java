package org.qgstudio.netty.config;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.log4j.Log4j2;
import org.qgstudio.netty.MyChannelInitializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Description: $
 * @Param: $
 * @return: $
 * @author: SheledonPeng
 * @Date: $
 */
@Log4j2
public class WebsocketConfig extends Thread{


    private static int websocketPort;

    public WebsocketConfig()  {
        try {
            // 读取配置文件
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("network.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            websocketPort = Integer.parseInt(properties.getProperty("websocketPort"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workGroup);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new MyChannelInitializer());
            log.info("websocket服务端已启动,端口为:" + websocketPort);
            Channel ch = b.bind(websocketPort).sync().channel();
            ch.closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            //优雅的退出程序
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
