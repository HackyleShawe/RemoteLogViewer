package com.hackyle.log.viewer.config;

import com.hackyle.log.viewer.handler.LogWebSocketHandler;
import com.hackyle.log.viewer.interceptor.WebSocketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Autowired
    private LogWebSocketHandler logWebSocketHandler;
    @Autowired
    private WebSocketInterceptor webSocketInterceptor;

    @Value("${websocket.paths}")
    private String websocketPaths;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        String[] paths = websocketPaths.split(",");

        registry
                .addHandler(logWebSocketHandler, paths)
                .addInterceptors(webSocketInterceptor)
                .setAllowedOrigins("*");
    }
}
