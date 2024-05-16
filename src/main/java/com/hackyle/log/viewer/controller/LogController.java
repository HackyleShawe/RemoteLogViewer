package com.hackyle.log.viewer.controller;

import com.hackyle.log.viewer.pojo.LogTargetBean;
import com.hackyle.log.viewer.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
public class LogController {
    @Autowired
    private List<LogTargetBean> logTargetBeanList;
    @Autowired
    private LogService logService;

    /**
     * 首页：展示所有已经配置了的日志抓取目标
     */
    @RequestMapping("/")
    public ModelAndView index(ModelAndView modelAndView) {
        modelAndView.addObject("targetList", logTargetBeanList);
        modelAndView.setViewName("index");

        return modelAndView;
    }

    /**
     * 日志实时抓取、查看、页内搜索页面
     * @param code 日志抓取目标的唯一标识
     * @return templates/log.html
     */
    @RequestMapping("/log/{code}")
    public ModelAndView log(@PathVariable("code") String code, ModelAndView modelAndView) {
        LogTargetBean targetBean = null;
        for (LogTargetBean logTargetBean : logTargetBeanList) {
            if(logTargetBean.getCode().equals(code)) {
                targetBean = logTargetBean;
                break;
            }
        }

        if(null == targetBean) {
            return index(modelAndView);
        }

        modelAndView.addObject("logTarget", targetBean);
        modelAndView.setViewName("log");

        return modelAndView;
    }

    /**
     * 提供一个普通接口，强制关闭WebSocketServer端
     */
    @RequestMapping("/log/stop")
    @ResponseBody
    public String stopWebSocket(@RequestParam("sid") String sid) {
        if(null == sid || "".equals(sid.trim())) {
            return "SessionID缺失";
        }

        return logService.closeWebSocketServer(sid) ? "WebSocketServer关闭成功" : "WebSocketServer关闭失败";
    }

}
