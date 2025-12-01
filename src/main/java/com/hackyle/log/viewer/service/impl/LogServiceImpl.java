package com.hackyle.log.viewer.service.impl;

import com.hackyle.log.viewer.pojo.SessionDomain;
import com.hackyle.log.viewer.service.LogService;
import com.hackyle.log.viewer.util.JschUtils;
import com.hackyle.log.viewer.ws.SessionDomainHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private SessionDomainHolder sessionDomainHolder;


    @Override
    public boolean closeWebSocketServer(String sid) {
        SessionDomain sessionDomain = sessionDomainHolder.getSessionDomain(sid);
        if(null != sessionDomain) {
            try {
                //关闭WebSocket、SSH的连接会话
                sessionDomain.getWebSocketSession().close();
                JschUtils.releaseSshSession(sessionDomain.getSshSession());
                return true;
            } catch (IOException e) {
                System.out.println("closeWebSocketServer出现异常：" + e);
            }
        }
        return true;
    }
}
