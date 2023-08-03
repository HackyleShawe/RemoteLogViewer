package com.hackyle.log.viewer.util;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.util.Properties;

/**
 * 使用jsch工具模拟SSH客户端，与SSH服务端建立连接
 */
public class JschUtils {

    public static Session buildSshSession(String host, int port, String username, String password) {
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
                throw new RuntimeException("SSH连接失败" + sshSession.getHost() + ":" + sshSession.getPort() +"  "+ sshSession);
            }
        } catch (Exception e) {
            System.out.println("SSH连接出现异常：" + e);
        }

        return sshSession;
    }

    public static void releaseSshSession(Session sshSession) {
        if(sshSession != null) {
            sshSession.disconnect();
            if(!sshSession.isConnected()) {
                System.out.println("SSH已断开连接：" + sshSession.getHost()+":"+ sshSession.getPort() +"  "+ sshSession);
            }
        }
    }

}
