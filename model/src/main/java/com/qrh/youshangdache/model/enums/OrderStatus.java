package com.qrh.youshangdache.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum OrderStatus {
    WAITING_ACCEPT(1, "等待接单"),
    ACCEPTED(2, "司机已接单"),
    DRIVER_ARRIVED(3, "司机已到达上车位置"),
    START_SERVICE(4, "开始代驾"),
    END_SERVICE(5, "结束代驾"),
    ORDER_UNPAID(6, "未付款"),
    ORDER_PAID(7, "已付款"),
    ORDER_FINISHED(8, "订单已结束"),
    ORDER_CANCELED_BY_USER(9, "用户撤单"),
    ORDER_CANCELED_BY_DRIVER(10, "司机撤单"),
    ORDER_CLOSED_CASE_ACCIDENT(11, "订单因事故关闭"),
    UPDATE_CART_INFO(13, "更新代驾车辆信息"),
    ORDER_CANCELED_WITH_NO_DRIVER_ACCEPT_ORDER(-1, "没有司机接单，取消订单"),
    ORDER_NOT_EXIST(-100, "订单不存在");
    /**
     *
     * 订单状态
     */
    @EnumValue
    private final Integer status;
    /**
     * 订单状态描述
     */
    private final String comment;

    OrderStatus(Integer status, String comment) {
        this.status = status;
        this.comment = comment;
    }

}
