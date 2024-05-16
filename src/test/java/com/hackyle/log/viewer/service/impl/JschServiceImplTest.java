package com.hackyle.log.viewer.service.impl;

import com.hackyle.log.viewer.util.JschUtils;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

//@SpringBootTest
class JschServiceImplTest {

    //@Test
    public void testConnect() throws Exception {
        //建立SSH连接后要执行的命令
        String remoteCommand = "date";
        String host = "10.1.1.1";
        int port = 22;
        String username = "root";
        String password = "hackyle";

        Session sshSession = JschUtils.buildSshSession(host, port, username, password);
        ChannelExec channelExec = (ChannelExec) sshSession.openChannel("exec");
        channelExec.setCommand(remoteCommand);
        channelExec.connect();
        InputStream is = channelExec.getInputStream();

        //包装为字符流，方便每次读取一行
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        String buf = "";
        System.out.println("命令 " + remoteCommand + " 的执行结果：");
        while ((buf = reader.readLine()) != null) {
            System.out.println(buf);
        }

        is.close();
        JschUtils.releaseSshSession(sshSession);

        //输出
        //连接成功，会话：com.jcraft.jsch.Session@59018eed
        //命令 date 的执行结果：
        //Mon Dec  5 11:16:12 CST 2022
        //session状态：false
    }

}
