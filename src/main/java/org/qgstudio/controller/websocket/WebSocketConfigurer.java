package org.qgstudio.controller.websocket;

import org.qgstudio.service.impl.WebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Description: websocket的配置类
 * @Param:
 * @return:
 * @Author: SheldonPeng
 * @Date: 2019-07-26
 */

@Configuration
@EnableWebSocket
public class WebSocketConfigurer implements org.springframework.web.socket.config.annotation.WebSocketConfigurer {


    private static String url;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        try {
            // 读取配置文件
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("network.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            url = properties.getProperty("websocketUrl");
            // 注册websocket
            registry.addHandler(myHandler(),url).setAllowedOrigins("*");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Bean
    public WebSocketHandler myHandler() {
        return new WebSocketHandler();
    }
}
