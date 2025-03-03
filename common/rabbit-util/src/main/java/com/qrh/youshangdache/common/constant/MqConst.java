package com.qrh.youshangdache.common.constant;

/**
 * rabbitmq相关的常量
 */
public class MqConst {


    public static final String EXCHANGE_ORDER = "daijia.order";
    public static final String ROUTING_PAY_SUCCESS = "daijia.pay.success";
    public static final String ROUTING_PROFITSHARING_SUCCESS = "daijia.profitsharing.success";
    public static final String QUEUE_PAY_SUCCESS = "daijia.pay.success";
    public static final String QUEUE_PROFITSHARING_SUCCESS = "daijia.profitsharing.success";


    /**
     * 取消订单延迟消息的交换机名
     */
    public static final String EXCHANGE_CANCEL_ORDER = "daijia.cancel.order";
    /**
     * 取消订单延迟消息的路由key
     */
    public static final String ROUTING_CANCEL_ORDER = "daijia.cancel.order";
    /**
     * 取消订单延迟消息的队列名
     */
    public static final String QUEUE_CANCEL_ORDER = "daijia.cancel.order";

    /**
     * 分账交换机名
     */
    public static final String EXCHANGE_PROFITSHARING = "daijia.profitsharing";
    /**
     * 分账路由key
     */
    public static final String ROUTING_PROFITSHARING = "daijia.profitsharing";
    /**
     * 分账队列名
     */
    public static final String QUEUE_PROFITSHARING = "daijia.profitsharing";

}
