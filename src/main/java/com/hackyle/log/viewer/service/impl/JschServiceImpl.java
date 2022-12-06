package com.hackyle.log.viewer.service.impl;

import com.hackyle.log.viewer.service.JschService;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * 使用jsch工具模拟SSH客户端，与SSH服务端建立连接
 */
@Service
public class JschServiceImpl implements JschService {
    /**
     * SSH连接参数
     */
    private String host;
    private String username;
    private int port;
    private String password;

    /**
     * 通过Spring注入配置文件中的数据
     */
    @Value("${jsch.host}")
    public void setHost(String host) {
        this.host = host;
    }

    @Value("${jsch.username}")
    public void setUsername(String username) {
        this.username = username;
    }

    @Value("${jsch.port}")
    public void setPort(int port) {
        this.port = port;
    }

    @Value("${jsch.password}")
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Session buildConnect() {
        Session sshSession = null;
        try {
            JSch jSch = new JSch(); //创建一个ssh通讯核心类
            sshSession = jSch.getSession(username, host, port); //传主机、端口、用户名获得一个会话

            Properties config = new Properties();
            config.put("StrictHostKeyChecking","no"); //不进行严格模式检查
            sshSession.setPassword(password); //设置密码
            sshSession.setConfig(config);

            sshSession.connect(); //连接会话

            if(sshSession.isConnected()) {
                System.out.println("SSH连接成功：" + sshSession.getHost() + ":" + sshSession.getPort() +"  "+ sshSession);
            } else {
                throw new RuntimeException("SSH连接失败");
            }
        } catch (Exception e) {
            System.out.println("SSH连接出现异常：" + e);
        }

        return sshSession;
    }

    @Override
    public void destroyConnect(Session sshSession) {
        if(sshSession != null) {
            sshSession.disconnect();
            if(!sshSession.isConnected()) {
                System.out.println("SSH已断开连接：" + sshSession.getHost()+":"+ sshSession.getPort() +"  "+ sshSession);
            }
        }
    }
}
