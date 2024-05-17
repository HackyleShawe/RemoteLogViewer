package com.hackyle.log.viewer.pojo;

import com.jcraft.jsch.Session;
import org.springframework.web.socket.WebSocketSession;

/**
 * 每个WebSocketSession关联的相关数据对象
 */
public class WsSessionBean {
    /** WebSocket Client与Server的连接会话ID */
    private String wsSessionId;

    /** WebSocket Client与Server的连接会话 */
    private WebSocketSession webSocketSession;

    /** 此个WS的连接参数信息 */
    private LogTargetBean logTargetBean;

    /** SSH连接会话 */
    private Session sshSession;

    /** 要捕获多少条历史日志 */
    private int historyItems;

    /** 要搜索的关键词，使用-分割 */
    private String keywords;

    public String getWsSessionId() {
        return wsSessionId;
    }

    public void setWsSessionId(String wsSessionId) {
        this.wsSessionId = wsSessionId;
    }

    public WebSocketSession getWebSocketSession() {
        return webSocketSession;
    }

    public void setWebSocketSession(WebSocketSession webSocketSession) {
        this.webSocketSession = webSocketSession;
    }

    public LogTargetBean getLogTargetBean() {
        return logTargetBean;
    }

    public void setLogTargetBean(LogTargetBean logTargetBean) {
        this.logTargetBean = logTargetBean;
    }

    public Session getSshSession() {
        return sshSession;
    }

    public void setSshSession(Session sshSession) {
        this.sshSession = sshSession;
    }

    public int getHistoryItems() {
        return historyItems;
    }

    public void setHistoryItems(int historyItems) {
        this.historyItems = historyItems;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
}
