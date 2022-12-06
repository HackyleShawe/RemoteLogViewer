package com.hackyle.log.viewer.handler;

import com.hackyle.log.viewer.pojo.WsSessionBean;
import com.hackyle.log.viewer.service.JschService;
import com.hackyle.log.viewer.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LogWebSocketHandler extends TextWebSocketHandler implements WebSocketHandler {
    /**
     * 保存与本个WebSocket建立起连接的客户端，Map<wsSessionId, wsSession Instance>
     */
    //private static Map<String, WebSocketSession> livingSessions = new ConcurrentHashMap<>();
    private static Map<String, WsSessionBean> livingSessionMap = new ConcurrentHashMap<>(); //使用线程安全的Map

    @Autowired
    private LogService logService;
    /** SSH连接 */
    @Autowired
    private JschService jschService;

    /**
     * 连接建立成功时调用
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("WebSocketServer连接建立成功：" + session.getId());

        //缓存当前已经创建的连接
        WsSessionBean wsSessionBean = new WsSessionBean();
        wsSessionBean.setWebSocketSession(session);
        wsSessionBean.setSshSession(jschService.buildConnect());
        livingSessionMap.put(session.getId(), wsSessionBean);

        //先把SessionId发给前端，规定好一个格式，方便前端判定
        session.sendMessage(new TextMessage("sessionId:"+session.getId()));

        //WebSocket的前后端建立连接成功，立即调用日志推动逻辑，将数据推送给客户端
        logService.sendLog2BrowserClient(wsSessionBean);
    }

    /**
     * 当客户端有消息发来时调用
     * @param session 客户端连接
     * @param message 传来的消息
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        System.out.println("WebSocketServer收到客户端"+ session.getId() +"的消息：" + message.getPayload());
    }

    /**
     * 当有出错信息时调用
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        livingSessionMap.remove(session.getId());

        System.out.println("WebSocketServer出现错误：" + session.getId() + exception);
    }

    /**
     * 关闭连接后调用
     * @param session 连接
     * @param status 状态
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        livingSessionMap.remove(session.getId());
        System.out.println("WebSocketServer已关闭：" + session.getId());
    }

    public boolean closeWebSocketServer(String sid) {
        WsSessionBean wsSessionBean = livingSessionMap.get(sid);
        if(null != wsSessionBean) {
            try {
                //关闭WebSocket、SSH的连接会话
                wsSessionBean.getWebSocketSession().close();
                jschService.destroyConnect(wsSessionBean.getSshSession());

                return true;
            } catch (IOException e) {
                System.out.println("closeWebSocketServer出现异常："+e);
            }
        }

        return false;
    }

}
