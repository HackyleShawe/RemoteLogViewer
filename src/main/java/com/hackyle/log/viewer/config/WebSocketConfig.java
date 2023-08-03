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

    @Value("${websocket.endpoints}")
    private String websocketEndpoints;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        String[] paths = websocketEndpoints.split(",");

        registry.addHandler(logWebSocketHandler, paths) //向这些paths，推送logWebSocketHandler这个处理器中的数据
                .addInterceptors(webSocketInterceptor) //注入WebSocket拦截器
                .setAllowedOrigins("*"); //允许跨域访问
    }
}
