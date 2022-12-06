package com.hackyle.log.viewer.service.impl;

import com.hackyle.log.viewer.handler.LogWebSocketHandler;
import com.hackyle.log.viewer.pojo.WsSessionBean;
import com.hackyle.log.viewer.service.LogService;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
public class LogServiceImpl implements LogService {

    /** 通过SSH连接后要执行的Shell命令 */
    @Value("${log-path}")
    private String logPath;

    @Autowired
    private LogWebSocketHandler logWebSocketHandler;

    /**
     * 主要逻辑
     * 1. 准备要执行的Shell命令：tail -1f 日志文件的绝对路径，例如：tail -1f /data/blog.hackyle.com/blog-business-logs/blog-business.log
     * 2. 获取sshSession，创建一个执行Shell命令的Channel
     * 3. 从Channel中读取流，包装为字符流，一次读取一行日志数据
     * 4. 获取WebSocket Session，只要它没有被关闭，就将日志数据通过该Session推送出去
     *
     * @param wsSessionBean 前端Client与后端WebSocketServer建立的连接实例
     */
    @Override
    public void sendLog2BrowserClient(WsSessionBean wsSessionBean) throws Exception {
        WebSocketSession wsSession = wsSessionBean.getWebSocketSession();
        Session sshSession = wsSessionBean.getSshSession();

        //从域对象中获取调用WebSocketClient传递过来的参数
        Object countObj = wsSession.getAttributes().get("count");
        int count = 1;
        if(countObj != null) {
            try {
                count = Integer.parseInt(String.valueOf(countObj).trim());
            } catch (Exception e) {
                System.out.println("read2Integer转换出现异常：" + e);
            }
        }

        //String command = "ssh tpbbsc01 \"tail -" +count+ "f " +logPath+ "\""; //二级SSH跳板机在这里修改
        String command = "tail -" +count+ "f " +logPath;
        System.out.println("command: " + command);

        //创建一个执行Shell命令的Channel
        ChannelExec channelExec = (ChannelExec) sshSession.openChannel("exec");
        channelExec.setCommand(command);
        channelExec.connect();
        InputStream inputStream = channelExec.getInputStream();

        //包装为字符流，方便每次读取一行
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String buf = "";
        while ((buf = reader.readLine()) != null) {
            if(wsSession.isOpen()) {
                //往WebSocket中推送数据
                wsSession.sendMessage(new TextMessage(buf));
            }

        }

        //while(wsSession.isOpen()) {
        //    buf = reader.readLine();
        //    //System.out.println(buf.length());
        //    if(null != buf && buf.length() > 0 && wsSession.isOpen()) {
        //        //往WebSocket中推送数据
        //        wsSession.sendMessage(new TextMessage(buf));
        //    }
        //}

        //WebSocket、SSH Session的关闭，通过本类下的‘closeWebSocketServer’方法控制
    }

    @Override
    public boolean closeWebSocketServer(String sid) {
        return logWebSocketHandler.closeWebSocketServer(sid);
    }
}
