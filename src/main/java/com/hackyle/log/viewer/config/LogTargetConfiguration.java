package com.hackyle.log.viewer.config;

import com.hackyle.log.viewer.pojo.LogTargetBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 从YML配置文件注入要抓取的哪些日志目标
 */
@ConfigurationProperties(prefix = "log")
@EnableConfigurationProperties(LogTargetConfiguration.class)
@Configuration
public class LogTargetConfiguration {
    private List<LogTargetBean> targets;

    @Bean
    public List<LogTargetBean> logTargetBeanList() {
        return targets;
    }


    public List<LogTargetBean> getTargets() {
        return targets;
    }

    public void setTargets(List<LogTargetBean> targets) {
        this.targets = targets;
    }

}


