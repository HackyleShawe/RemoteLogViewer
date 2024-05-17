package com.hackyle.log.viewer.service.impl;

import com.hackyle.log.viewer.handler.LogWebSocketHandler;
import com.hackyle.log.viewer.pojo.LogTargetBean;
import com.hackyle.log.viewer.pojo.WsSessionBean;
import com.hackyle.log.viewer.service.LogService;
import com.hackyle.log.viewer.util.JschUtils;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LogWebSocketHandler logWebSocketHandler;

    /**
     * 注入要抓取的日志目标列表
     */
    @Autowired
    private List<LogTargetBean> logTargetBeanList;


    @Override
    public void sendRealtimeLog(WebSocketSession wsSession) throws Exception {
        //创建WebSocket与SSH连接，并缓存起来
        WsSessionBean wsSessionBean = buildWsSession4RealtimeLog(wsSession);

        //实时推送日志数据到客户端
        sendRealtimeLogToWebSocketClient(wsSessionBean);
    }

    private WsSessionBean buildWsSession4RealtimeLog(WebSocketSession wsSession) throws Exception {
        WsSessionBean wsSessionBean = new WsSessionBean();
        wsSessionBean.setWebSocketSession(wsSession);

        Object targetCode = wsSession.getAttributes().get("targetCode"); //当前是那个日志目标
        LogTargetBean targetBean = null;
        for (LogTargetBean logTargetBean : logTargetBeanList) {
            if(logTargetBean.getCode().equals(String.valueOf(targetCode))) {
                targetBean = logTargetBean;
                break;
            }
        }
        if(null == targetBean) {
            wsSession.close();
            throw new Exception("日志目标不存在！");
        }
        wsSessionBean.setLogTargetBean(targetBean);

        Object historyItemsObj = wsSession.getAttributes().get("historyItems"); //要捕获多少条历史日志
        int historyItems = 1;
        if(historyItemsObj != null) {
            try {
                historyItems = Integer.parseInt(String.valueOf(historyItemsObj).trim());
            } catch (Exception e) {
                System.out.println("read2Integer转换出现异常：" + e);
            }
        }
        wsSessionBean.setHistoryItems(historyItems);

        Session sshSession = JschUtils.buildSshSession(targetBean.getHost(), targetBean.getPort(), targetBean.getUsername(), targetBean.getPassword());
        wsSessionBean.setSshSession(sshSession);

        //缓存当前已经创建的连接
        LogWebSocketHandler.getLivingSessionMap().putIfAbsent(wsSession.getId(), wsSessionBean);
        //if(null == livingSessionMap.get(wsSession.getId())) {
        //    //缓存当前已经创建的连接
        //    livingSessionMap.put(wsSession.getId(), wsSessionBean);
        //}

        //先把SessionId发给前端，规定好一个格式，方便前端判定
        wsSession.sendMessage(new TextMessage("sessionId:"+wsSession.getId()));

        return wsSessionBean;
    }


    /**
     * 主要逻辑
     * 1. 准备要执行的Shell命令：tail -1f 日志文件的绝对路径，例如：tail -1f /data/blog.hackyle.com/blog-business-logs/blog-business.log
     * 2. 获取sshSession，创建一个执行Shell命令的Channel
     * 3. 从Channel中读取流，包装为字符流，一次读取一行日志数据
     * 4. 获取WebSocket Session，只要它没有被关闭，就将日志数据通过该Session推送出去
     *
     * @param wsSessionBean 前端Client与后端WebSocketServer建立的连接实例
     */
    private void sendRealtimeLogToWebSocketClient(WsSessionBean wsSessionBean) throws Exception {
        WebSocketSession wsSession = wsSessionBean.getWebSocketSession();
        Session sshSession = wsSessionBean.getSshSession();

        //String command = "ssh tpbbsc01 \"tail -" +count+ "f " +logPath+ "\""; //二级SSH跳板机在这里修改
        String command = "tail -" +wsSessionBean.getHistoryItems()+ "f " + wsSessionBean.getLogTargetBean().getLogPath();
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
    public void sendSearchLog(WebSocketSession wsSession) throws Exception {
        //创建WebSocket与SSH连接，并缓存起来
        WsSessionBean wsSessionBean = buildWsSession4SearchLog(wsSession);

        //实时推送日志数据到客户端
        sendSearchLogToWebSocketClient(wsSessionBean);
    }

    private WsSessionBean buildWsSession4SearchLog(WebSocketSession wsSession) throws Exception {
        WsSessionBean wsSessionBean = new WsSessionBean();
        wsSessionBean.setWebSocketSession(wsSession);

        Object targetCode = wsSession.getAttributes().get("targetCode"); //当前是那个日志目标
        LogTargetBean targetBean = null;
        for (LogTargetBean logTargetBean : logTargetBeanList) {
            if(logTargetBean.getCode().equals(String.valueOf(targetCode))) {
                targetBean = logTargetBean;
                break;
            }
        }
        if(null == targetBean) {
            wsSession.close();
            throw new Exception("日志目标不存在！");
        }
        wsSessionBean.setLogTargetBean(targetBean);

        String keywords = wsSession.getAttributes().get("keywords").toString(); //要捕获多少条历史日志
        wsSessionBean.setKeywords(keywords);

        Session sshSession = JschUtils.buildSshSession(targetBean.getHost(), targetBean.getPort(), targetBean.getUsername(), targetBean.getPassword());
        wsSessionBean.setSshSession(sshSession);

        //缓存当前已经创建的连接
        LogWebSocketHandler.getLivingSessionMap().putIfAbsent(wsSession.getId(), wsSessionBean);

        //先把SessionId发给前端，规定好一个格式，方便前端判定
        wsSession.sendMessage(new TextMessage("sessionId:"+wsSession.getId()));

        return wsSessionBean;
    }

    /**
     *  主要逻辑
     *  1.准备要执行的Shell命令：grep [OPTION]... PATTERN 日志文件的绝对路径，例如：grep -i "登录入参" /data/blog.hackyle.com/log-business-logs/blog-business.log
     *  2.获取sshSession，创建一个执行Shell命令的Channel
     *  3.从Channel中读取流，包装为字符流，一次读取一行日志数据
     *  4.获取WebSocket Session，只要它没有被关闭，就将日志数据通过该Session推送出去
     */
    private void sendSearchLogToWebSocketClient(WsSessionBean wsSessionBean) throws Exception {
        WebSocketSession wsSession = wsSessionBean.getWebSocketSession();
        Session sshSession = wsSessionBean.getSshSession();

        String keywords = wsSessionBean.getKeywords();
        String[] ksArr = keywords.split("-");

        String command = "";
        if(ksArr.length == 1) { //只有一个关键字，直接搜索
            //-E:支持正则，-i:忽略大小写
            command = "grep -E -i \"" + keywords + "\" " + wsSessionBean.getLogTargetBean().getLogPath();
        } else { //多个关键字
            String kws = String.join("|", ksArr);
            command = "grep -E -i \"" + kws + "\" " + wsSessionBean.getLogTargetBean().getLogPath();
        }

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
    }

    @Override
    public boolean closeWebSocketServer(String sid) {
        return logWebSocketHandler.closeWebSocketServer(sid);
    }
}
