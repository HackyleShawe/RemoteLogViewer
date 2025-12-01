package com.hackyle.log.viewer.ws;

import com.hackyle.log.viewer.pojo.SessionDomain;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 每一个WS连接，就是一次WS会话；暂存WebSocket Session
 */
@Component
//@Scope("singleton") Spring默认单例，这里强调一下
public class SessionDomainHolder {
    /**
     * 保存与本个WebSocket建立起连接的客户端，Map<wsSessionId, wsSession Instance>
     */
    private Map<String, SessionDomain> domainSessionMap = new ConcurrentHashMap<>(); //使用线程安全的Map

    public SessionDomain getSessionDomain(String sessionId) {
        return domainSessionMap.get(sessionId);
    }

    public boolean addSessionDomain(String sessionId, SessionDomain sessionDomain) {
        return domainSessionMap.putIfAbsent(sessionId, sessionDomain) != null;
    }

    public boolean removeSessionDomain(String sessionId) {
        return domainSessionMap.remove(sessionId) != null;
    }

    //TODO: ws session淘汰策略（选择最老的数据淘汰），防止内存泄漏

}
