package com.hackyle.log.viewer.interceptor;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
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

            String requestURL = httpRequest.getRequestURL().toString();
            //System.out.println("WebSocket请求路径：" + requestURI);

            String targetCode = httpRequest.getParameter("targetCode");
            attributes.put("targetCode", targetCode); //当前是那个日志目标

            if(requestURL.contains("/ws/log")) { //实时日志
                //获取调用WebSocket传递过来的参数，放入域对象中
                String historyItems = httpRequest.getParameter("historyItems");
                attributes.put("historyItems", historyItems); //要捕获多少条历史日志

            } else if(requestURL.contains("/ws/search")) { //搜索日志
                String keywords = httpRequest.getParameter("keywords");
                if(StringUtils.isEmpty(keywords)) {
                    System.out.println("关键字不能为空，已拦截");
                    return false;
                } else {
                    attributes.put("keywords", keywords); //要搜索的关键字
                }

            } else {
                System.out.println("其他非法请求，已拦截");
                return false;
            }
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
