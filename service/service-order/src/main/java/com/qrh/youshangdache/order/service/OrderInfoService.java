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
    /**
     * 保存订单信息
     *
     * @param orderInfoForm 订单信息对象
     * @return 订单id
     */
    Long saveOrderInfo(OrderInfoForm orderInfoForm);

    /**
     * 乘客下完单后，订单状态为1，乘客端小程序会轮询订单状态，当订单状态为2时，说明已经有司机接单了，那么页面进行跳转，进行下一步操作
     *
     * @param orderId 订单id
     * @return 订单状态代号
     */
    Integer getOrderStatus(Long orderId);

    Boolean robNewOrder(Long driverId, Long orderId);

    /**
     * 乘客如果已经下过单了，而且这个订单在执行中，没有结束，
     * 那么乘客是不可以再下单的，页面会弹出层，进入执行中的订单。
     *
     * @param customerId 用户id
     * @return 当前用户正在进行的订单信息
     */
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

    /**
     * 根据订单Id 取消订单
     *
     * @param orderId 订单id
     */
    void orderCancel(Long orderId);

    Boolean updateCouponAmount(Long orderId, BigDecimal couponAmount);

    /**
     * 系统取消订单
     *
     * @param orderId 订单id
     */
    void systemCancelOrder(Long orderId);

    void updateProfitsharingStatus(String orderNo);


}
