package com.qrh.youshangdache.common.service;


import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitService {

    @Resource
    private RabbitTemplate rabbitTemplate;

    //发送消息
    public boolean sendMessage(String exchange,
                               String routingKey,
                               Object message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        return true;
    }

}
