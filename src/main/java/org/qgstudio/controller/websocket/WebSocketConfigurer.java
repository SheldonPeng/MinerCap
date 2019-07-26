package org.qgstudio.controller.websocket;

import org.qgstudio.service.impl.WebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

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


    private final String url = "/websocket";

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        registry.addHandler(myHandler(),url).setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler myHandler() {
        return new WebSocketHandler();
    }
}
