package com.qrh.youshangdache.order.service;

import com.qrh.youshangdache.model.entity.order.OrderInfo;
import com.qrh.youshangdache.model.form.order.OrderInfoForm;
import com.qrh.youshangdache.model.form.order.StartDriveForm;
import com.qrh.youshangdache.model.form.order.UpdateOrderBillForm;
import com.qrh.youshangdache.model.form.order.UpdateOrderCartForm;
import com.qrh.youshangdache.model.vo.base.PageVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qrh.youshangdache.model.vo.order.*;

import java.math.BigDecimal;

public interface OrderInfoService extends IService<OrderInfo> {
    Long saveOrderInfo(OrderInfoForm orderInfoForm);

    Integer getOrderStatus(Long orderId);

    Boolean robNewOrder(Long driverId, Long orderId);

    CurrentOrderInfoVo searchCustomerCurrentOrder(Long customerId);

    CurrentOrderInfoVo searchDriverCurrentOrder(Long driverId);

    Boolean driverArriveStartLocation(Long orderId, Long driverId);

    Boolean updateOrderCart(UpdateOrderCartForm updateOrderCartForm);

    Boolean startDrive(StartDriveForm startDriveForm);

    Long getOrderNumByTime(String startTime, String endTime);

    Boolean endDrive(UpdateOrderBillForm updateOrderBillForm);

    PageVo findCustomerOrderPage(Page<OrderInfo> pageParam, Long customerId);

    PageVo findDriverOrderPage(Page<OrderInfo> pageParam, Long driverId);

    OrderBillVo getOrderBillInfo(Long orderId);

    OrderProfitsharingVo getOrderProfitsharing(Long orderId);

    Boolean sendOrderBillInfo(Long orderId, Long driverId);

    OrderPayVo getOrderPayVo(String orderNo, Long customerId);

    Boolean updateOrderPayStatus(String orderNo);

    OrderRewardVo getOrderRewardFee(String orderNo);

    void orderCancel(Long parseLong);

    Boolean updateCouponAmount(Long orderId, BigDecimal couponAmount);

    /**
     * 系统取消订单
     * @param orderId 订单id
     */
    void systemCancelOrder(Long orderId);
}
