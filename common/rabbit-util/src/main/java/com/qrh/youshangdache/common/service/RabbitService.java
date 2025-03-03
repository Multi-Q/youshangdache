package com.qrh.youshangdache.common.service;


import com.alibaba.fastjson2.JSON;
import com.qrh.youshangdache.common.entity.GuiguCorrelationData;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class RabbitService {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     *  发送消息
     * @param exchange 交换机
     * @param routingKey 路由键
     * @param message 消息内容
     */
    public boolean sendMessage(String exchange, String routingKey, Object message) {
        //1.创建自定义相关消息对象-包含业务数据本身，交换器名称，路由键，队列类型，延迟时间,重试次数
        GuiguCorrelationData correlationData = new GuiguCorrelationData();
        String uuid = "mq:" + UUID.randomUUID().toString().replaceAll("-", "");
        correlationData.setId(uuid);
        correlationData.setMessage(message);
        correlationData.setExchange(exchange);
        correlationData.setRoutingKey(routingKey);
        //2.将相关消息封装到发送消息方法中

        rabbitTemplate.convertAndSend(exchange, routingKey, message, correlationData);

        //3.将相关消息存入Redis  Key：UUID  相关消息对象  10 分钟
        stringRedisTemplate.opsForValue().set(uuid, JSON.toJSONString(correlationData), 10, TimeUnit.MINUTES);

        //log.info("生产者发送消息成功：{}，{}，{}", exchange, routingKey, message);
        return true;
    }

    /**
     * 发送延迟消息方法
     * @param exchange 交换机
     * @param routingKey 路由键
     * @param message 消息数据
     * @param delayTime 延迟时间，单位为：秒
     */
    public boolean sendDelayMessage(String exchange, String routingKey, Object message, int delayTime) {
        //1.创建自定义相关消息对象-包含业务数据本身，交换器名称，路由键，队列类型，延迟时间,重试次数
        GuiguCorrelationData correlationData = new GuiguCorrelationData();
        String uuid = "mq:" + UUID.randomUUID().toString().replaceAll("-", "");
        correlationData.setId(uuid);
        correlationData.setMessage(message);
        correlationData.setExchange(exchange);
        correlationData.setRoutingKey(routingKey);
        correlationData.setDelay(true);
        correlationData.setDelayTime(delayTime);

        //2.将相关消息封装到发送消息方法中
        rabbitTemplate.convertAndSend(exchange, routingKey, message,message1 -> {
            message1.getMessageProperties().setDelay(delayTime*1000);
            return message1;
        }, correlationData);

        //3.将相关消息存入Redis  Key：UUID  相关消息对象  10 分钟
        stringRedisTemplate.opsForValue().set(uuid, JSON.toJSONString(correlationData), 10, TimeUnit.MINUTES);
        return true;
    }

}
