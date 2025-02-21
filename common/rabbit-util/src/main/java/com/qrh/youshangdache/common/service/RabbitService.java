package com.qrh.youshangdache.common.service;


import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitService {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息到rabbitMQ
     *
     * @param exchange   交换机名字
     * @param routingKey 路由键
     * @param message    消息
     */
    public void sendMessage(String exchange,
                            String routingKey,
                            Object message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }

}
