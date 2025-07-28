package com.qrh.youshangdache.customer.service;

import com.qrh.youshangdache.model.entity.order.OrderInfo;
import com.qrh.youshangdache.model.form.customer.ExpectOrderForm;
import com.qrh.youshangdache.model.form.customer.SubmitOrderForm;
import com.qrh.youshangdache.model.form.map.CalculateDrivingLineForm;
import com.qrh.youshangdache.model.form.payment.CreateWxPaymentForm;
import com.qrh.youshangdache.model.vo.base.PageVo;
import com.qrh.youshangdache.model.vo.customer.ExpectOrderVo;
import com.qrh.youshangdache.model.vo.driver.DriverInfoVo;
import com.qrh.youshangdache.model.vo.map.DrivingLineVo;
import com.qrh.youshangdache.model.vo.map.OrderLocationVo;
import com.qrh.youshangdache.model.vo.map.OrderServiceLastLocationVo;
import com.qrh.youshangdache.model.vo.order.OrderInfoVo;
import com.qrh.youshangdache.model.vo.payment.WxPrepayVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface OrderService {

    ExpectOrderVo expectOrder(ExpectOrderForm expectOrderForm);
    /**
     * 乘客提交打车订单
     * @param submitOrderForm 订单信息对象
     * @return 订单号
     */
    Long submitOrder(SubmitOrderForm submitOrderForm);
    /**
     * 乘客下完单后，订单状态为1（等待接单），乘客端小程序会轮询订单状态，当订单状态为2（司机已接单）时，说明已经有司机接单了，那么页面进行跳转，进行下一步操作
     * @param orderId 订单id
     * @return 订单状态代号
     */
    Integer getOrderStatus(Long orderId);
    OrderInfoVo getOrderInfoByOrderId(Long orderId, Long customerId);

    DriverInfoVo getDriverInfo(Long orderId,Long customerId);


    OrderLocationVo getCacheOrderLocation(Long orderId );

    DrivingLineVo calculateDriverLine(CalculateDrivingLineForm calculateDrivingLineForm);

    OrderServiceLastLocationVo getOrderServiceLastLocation(Long orderId);

    PageVo findCustomerOrderPage(Page<OrderInfo> pageParam, Long customerId);

    WxPrepayVo createWxPayment(CreateWxPaymentForm createWxPaymentForm);

    Boolean queryPayStatus(String orderNo);
}
