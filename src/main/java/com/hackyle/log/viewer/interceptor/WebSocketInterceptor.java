package com.hackyle.log.viewer.interceptor;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * WebSocket拦截器，功能与SpringMVC拦截器类似
 */
@Component
public class WebSocketInterceptor implements HandshakeInterceptor {
    /**
     * 在握手前触发
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        //System.out.println("————————————————————WebSocketInterceptor-beforeHandshake————————————————————");

        //获取前端的请求参数
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest httpRequest = ((ServletServerHttpRequest) request).getServletRequest();

            //获取调用WebSocket传递过来的参数，放入域对象中
            String count = httpRequest.getParameter("count");
            attributes.put("count", count);
        }

        return true;
    }

    /**
     * 在握手后触发
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        //System.out.println("————————————————————WebSocketInterceptor-afterHandshake————————————————————");
        System.out.println("——————————————————————————WebSocketServer Ready——————————————————————————");

    }
}
