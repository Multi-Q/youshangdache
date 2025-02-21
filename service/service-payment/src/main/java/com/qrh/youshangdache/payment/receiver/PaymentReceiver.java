package com.qrh.youshangdache.payment.receiver;

import com.qrh.youshangdache.common.constant.MqConst;
import com.qrh.youshangdache.payment.service.WxPayService;
import com.rabbitmq.client.Channel;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author QRH
 * @date 2024/8/25 16:09
 * @description TODO
 */
@Component
public class PaymentReceiver {
    @Resource
    private WxPayService wxPayService;

    @RabbitListener(
            bindings = {
                    @QueueBinding(
                            value = @Queue(value = MqConst.QUEUE_PAY_SUCCESS, durable = "ture"),
                            exchange = @Exchange(value = MqConst.EXCHANGE_ORDER),
                            key = {MqConst.ROUTING_PAY_SUCCESS}
                    )
            }
    )
    public void paySuccess(String orderNo, Message message, Channel channel){
        wxPayService.handlerOrder(orderNo);

    }
}
