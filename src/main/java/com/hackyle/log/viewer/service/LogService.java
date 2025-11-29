package com.hackyle.log.viewer.service;


public interface LogService {

    /**
     * 关闭WebSocketServer端
     */
    boolean closeWebSocketServer(String sid);
}
