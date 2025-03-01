package com.qrh.youshangdache.mq.receiver;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.qrh.youshangdache.mq.config.DeadLetterMqConfig;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class ConfirmReceiver {

    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(value = "exchange.confirm",durable = Exchange.TRUE),
            value = @Queue(value = "queue.confirm",durable = "true"),
            key = "routing.confirm"))
    public void process(Message message, Channel channel) {

        System.out.println("RabbitListener:" + new String(message.getBody()));

        // false 确认一个消息，true 批量确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 监听延迟消息
     * @param msg
     * @param message
     * @param channel
     */
    @RabbitListener(queues = {DeadLetterMqConfig.QUEUE_DEAD_2})
    public void getDeadLetterMsg(String msg, Message message, Channel channel) {
        try {
            if (StringUtils.isNotBlank(msg)) {
                log.info("死信消费者：{}", msg);
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("[xx服务]监听xxx业务异常：{}", e);
        }
    }

}