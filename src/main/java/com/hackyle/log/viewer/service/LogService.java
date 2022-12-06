package com.hackyle.log.viewer.service;

import com.hackyle.log.viewer.pojo.WsSessionBean;

public interface LogService {

    /**
     * 发送日志数据给浏览器客户端
     * @param wsSessionBean 前端Client与后端WebSocketServer建立的连接实例
     */
    void sendLog2BrowserClient(WsSessionBean wsSessionBean) throws Exception;

    boolean closeWebSocketServer(String sid);
}
