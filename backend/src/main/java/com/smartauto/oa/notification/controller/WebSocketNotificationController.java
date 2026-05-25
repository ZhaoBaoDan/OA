package com.smartauto.oa.notification.controller;

import com.smartauto.oa.common.SecurityUtils;
import com.smartauto.oa.notification.entity.BizNotification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket 通知推送
 */
@Tag(name = "WebSocket 通知")
@RestController
@RequestMapping("/ws")
public class WebSocketNotificationController {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketNotificationController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 向指定用户推送通知
     */
    public void sendToUser(Long userId, String type, String title, String content) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", type);
        message.put("title", title);
        message.put("content", content);
        message.put("timestamp", System.currentTimeMillis());
        messagingTemplate.convertAndSendToUser(
                String.valueOf(userId),
                "/queue/notifications",
                message);
    }

    /**
     * 向所有用户广播通知
     */
    public void broadcast(String type, String title, String content) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", type);
        message.put("title", title);
        message.put("content", content);
        message.put("timestamp", System.currentTimeMillis());
        messagingTemplate.convertAndSend("/topic/notifications", message);
    }

    /**
     * 心跳检测
     */
    @MessageMapping("/heartbeat")
    public void handleHeartbeat(@Payload String message, Principal principal) {
        // 客户端心跳，保持连接
    }
}
