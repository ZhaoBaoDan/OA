package com.smartauto.oa.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置
 */
@Configuration
public class RabbitMQConfig {

    /** 系统通知队列 */
    public static final String QUEUE_SYSTEM_NOTICE = "queue.system.notice";
    /** 操作日志队列 */
    public static final String QUEUE_OPER_LOG = "queue.oper.log";
    /** 交换机 */
    public static final String EXCHANGE_SYSTEM = "exchange.system";
    /** 路由键 - 通知 */
    public static final String ROUTING_NOTICE = "system.notice";
    /** 路由键 - 操作日志 */
    public static final String ROUTING_OPER_LOG = "system.oper.log";

    @Bean
    public DirectExchange systemExchange() {
        return ExchangeBuilder.directExchange(EXCHANGE_SYSTEM).durable(true).build();
    }

    @Bean
    public Queue systemNoticeQueue() {
        return QueueBuilder.durable(QUEUE_SYSTEM_NOTICE).build();
    }

    @Bean
    public Queue operLogQueue() {
        return QueueBuilder.durable(QUEUE_OPER_LOG).build();
    }

    @Bean
    public Binding noticeBinding(Queue systemNoticeQueue, DirectExchange systemExchange) {
        return BindingBuilder.bind(systemNoticeQueue).to(systemExchange).with(ROUTING_NOTICE);
    }

    @Bean
    public Binding operLogBinding(Queue operLogQueue, DirectExchange systemExchange) {
        return BindingBuilder.bind(operLogQueue).to(systemExchange).with(ROUTING_OPER_LOG);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        // 消息确认回调
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                // 记录失败日志，后续可重试
                System.err.println("消息发送失败: " + cause);
            }
        });
        // 消息退回回调
        template.setReturnsCallback(returned -> {
            System.err.println("消息被退回: " + returned.getMessage() + ", 交换机: "
                    + returned.getExchange() + ", 路由键: " + returned.getRoutingKey());
        });
        template.setMandatory(true);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        factory.setPrefetchCount(10);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }
}
