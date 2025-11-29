/**
 * WebSocket专用包，专门存放WebSocket相关代码文件
 *
 * WebSocketConfig：配置类，整合endpoint、Handler、Interceptor
 * WebSocketInterceptor：拦截器，每个WS请求都会经过此拦截器
 * LogWebSocketHandler：处理器，处理WS建立连接、接收消息、关闭连接等处理事件
 * LogWebSocketService：业务逻辑处理，真正的日志逻辑处理
 * LogWsSessionHolder：WS会话缓存器
 */
package com.hackyle.log.viewer.ws;
