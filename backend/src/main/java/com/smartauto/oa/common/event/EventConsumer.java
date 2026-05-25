package com.smartauto.oa.common.event;

import com.rabbitmq.client.Channel;
import com.smartauto.oa.config.RabbitMQConfig;
import com.smartauto.oa.notification.entity.BizNotification;
import com.smartauto.oa.notification.service.IBizNotificationService;
import com.smartauto.oa.system.entity.SysOperLog;
import com.smartauto.oa.system.mapper.SysOperLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 事件消费者（基于 RabbitMQ）
 */
@Component
public class EventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    private final IBizNotificationService notificationService;
    private final SysOperLogMapper operLogMapper;

    public EventConsumer(IBizNotificationService notificationService, SysOperLogMapper operLogMapper) {
        this.notificationService = notificationService;
        this.operLogMapper = operLogMapper;
    }

    /**
     * 消费系统通知事件
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_SYSTEM_NOTICE)
    public void handleNotice(SystemNoticeEvent event, Channel channel,
                             @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            logger.info("消费通知事件: userId={}, title={}", event.getUserId(), event.getTitle());

            BizNotification notification = new BizNotification();
            notification.setType(event.getType());
            notification.setTitle(event.getTitle());
            notification.setContent(event.getContent());
            notification.setUserId(event.getUserId());
            notification.setIsRead(0);
            notificationService.create(notification);

            channel.basicAck(tag, false);
        } catch (Exception e) {
            logger.error("消费通知事件失败: {}", e.getMessage());
            try {
                channel.basicNack(tag, false, true); // 重新入队
            } catch (IOException ex) {
                logger.error("Nack 失败: {}", ex.getMessage());
            }
        }
    }

    /**
     * 消费操作日志事件
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_OPER_LOG)
    public void handleOperLog(OperLogEvent event, Channel channel,
                              @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            SysOperLog log = new SysOperLog();
            log.setTitle(event.getTitle());
            log.setMethod(event.getMethod());
            log.setRequestUrl(event.getRequestUrl());
            log.setRequestParam(event.getRequestParam());
            log.setResponseResult(event.getResponseResult());
            log.setStatus(event.getStatus());
            log.setErrorMsg(event.getErrorMsg());
            log.setOperIp(event.getOperIp());
            log.setOperName(event.getOperName());
            log.setCostTime(event.getCostTime());
            operLogMapper.insert(log);

            channel.basicAck(tag, false);
        } catch (Exception e) {
            logger.error("消费操作日志事件失败: {}", e.getMessage());
            try {
                channel.basicNack(tag, false, true);
            } catch (IOException ex) {
                logger.error("Nack 失败: {}", ex.getMessage());
            }
        }
    }
}
