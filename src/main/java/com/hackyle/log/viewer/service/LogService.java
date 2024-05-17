package com.hackyle.log.viewer.service;

import com.hackyle.log.viewer.pojo.WsSessionBean;
import org.springframework.web.socket.WebSocketSession;

public interface LogService {

    /**
     * 发送实时日志数据给浏览器客户端
     * @param wsSession 前端Client与后端WebSocketServer建立的连接实例
     */
    void sendRealtimeLog(WebSocketSession wsSession) throws Exception;

    /**
     * 发送查询日志数据给浏览器客户端
     * @param wsSession 前端Client与后端WebSocketServer建立的连接实例
     */
    void sendSearchLog(WebSocketSession wsSession) throws Exception;

    /**
     * 关闭WebSocketServer端
     */
    boolean closeWebSocketServer(String sid);
}
