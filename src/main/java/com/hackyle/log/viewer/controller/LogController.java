package com.hackyle.log.viewer.controller;

import com.hackyle.log.viewer.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogController {
    @Autowired
    private LogService logService;

    /**
     * 提供一个普通接口，强制关闭WebSocketServer端
     */
    @RequestMapping("/log/stopWebSocket")
    public boolean stopWebSocket(@RequestParam("sid") String sid) {
        if(null == sid || "".equals(sid.trim())) {
            return false;
        }

        return logService.closeWebSocketServer(sid);
    }
}
