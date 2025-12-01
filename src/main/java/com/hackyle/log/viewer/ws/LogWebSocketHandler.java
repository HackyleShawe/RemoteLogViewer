package com.hackyle.log.viewer.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * 处理器，处理WS建立连接、接收消息、关闭连接等处理事件
 */
@Component
public class LogWebSocketHandler extends TextWebSocketHandler implements WebSocketHandler {
    /**
     * 每一个WS连接，就是一次WS会话
     * 保存与本个WebSocket建立起连接的客户端，Map<wsSessionId, wsSession Instance>
     */
    @Autowired
    private SessionDomainHolder sessionDomainHolder;

    @Autowired
    private LogWebSocketService logWebSocketService;

    /**
     * 连接建立成功时调用
     * 1.创建WS会话
     * 2.接收前端传递的参数
     * 3.创建SSH连接会话
     * 4.根据前端传递的targetCode获取LogTargetBean
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession wsSession) {
        System.out.println("WebSocketServer收到客户端连接sessionID：" + wsSession.getId());
    }

    /**
     * 当客户端有消息发来时调用
     * @param session 客户端连接
     * @param message 传来的消息
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("WebSocketServer收到客户端"+ session.getId() +"的消息：" + payload);

        if(!payload.trim().isEmpty()) {
            String[] split = payload.split("&");
            for (String sp : split) {
                String[] keyValue = sp.split("=");
                session.getAttributes().put(keyValue[0], keyValue[1]);
            }
        }

        String wsClientURI = session.getUri().toString();

        //根据URI跳转到不同的处理逻辑
        if(wsClientURI.contains("/ws/log/realtime")) {
            logWebSocketService.sendRealtimeLog(session);
        } else {
            logWebSocketService.sendSearchLog(session);
        }
    }

    /**
     * 当有出错信息时调用
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        sessionDomainHolder.removeSessionDomain(session.getId());

        System.out.println("WebSocketServer出现错误：" + session.getId() + exception);
    }

    /**
     * 关闭连接后调用
     * @param session 连接
     * @param status 状态
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessionDomainHolder.removeSessionDomain(session.getId());
        System.out.println("WebSocketServer已关闭：" + session.getId());
    }

}
