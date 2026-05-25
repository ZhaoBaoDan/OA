package com.smartauto.oa.common.event;

import com.smartauto.oa.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 事件发布器（基于 RabbitMQ）
 */
@Component
public class EventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(EventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public EventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 发布系统通知事件
     */
    public void publishNotice(SystemNoticeEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_SYSTEM,
                    RabbitMQConfig.ROUTING_NOTICE,
                    event);
            logger.info("发布通知事件: userId={}, title={}", event.getUserId(), event.getTitle());
        } catch (Exception e) {
            logger.error("发布通知事件失败: {}", e.getMessage());
        }
    }

    /**
     * 发布操作日志事件
     */
    public void publishOperLog(OperLogEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_SYSTEM,
                    RabbitMQConfig.ROUTING_OPER_LOG,
                    event);
        } catch (Exception e) {
            logger.error("发布操作日志事件失败: {}", e.getMessage());
        }
    }

    /**
     * 发布通知（便捷方法）
     */
    public void publishNotice(String type, String title, String content, Long userId) {
        publishNotice(new SystemNoticeEvent(type, title, content, userId));
    }
}
