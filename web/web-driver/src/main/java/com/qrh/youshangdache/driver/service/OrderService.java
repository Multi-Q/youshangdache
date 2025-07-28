package com.qrh.youshangdache.driver.service;

import com.qrh.youshangdache.model.entity.order.OrderInfo;
import com.qrh.youshangdache.model.form.map.CalculateDrivingLineForm;
import com.qrh.youshangdache.model.form.order.OrderFeeForm;
import com.qrh.youshangdache.model.form.order.StartDriveForm;
import com.qrh.youshangdache.model.form.order.UpdateOrderCartForm;
import com.qrh.youshangdache.model.vo.base.PageVo;
import com.qrh.youshangdache.model.vo.map.DrivingLineVo;
import com.qrh.youshangdache.model.vo.order.CurrentOrderInfoVo;
import com.qrh.youshangdache.model.vo.order.NewOrderDataVo;
import com.qrh.youshangdache.model.vo.order.OrderInfoVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface OrderService {

    /**
     * 乘客下完单后，订单状态为1（等待接单），乘客端小程序会轮询订单状态，当订单状态为2（司机已接单）时，说明已经有司机接单了，那么页面进行跳转，进行下一步操作
     * @param orderId 订单id
     * @return 订单状态代号
     */
    Integer getOrderStatus(Long orderId);

    List<NewOrderDataVo> findNewOrderQueueData(Long driverId);

    CurrentOrderInfoVo searchDriverCurrentOrder(Long driverId);

    OrderInfoVo getOrderInfoByOrderId(Long orderId, Long driverId);

    DrivingLineVo calculateDrivingLine(CalculateDrivingLineForm calculateDrivingLineForm);

    Boolean driverArriveStartLocation(Long orderId, Long driverId);

    Boolean updateOrderCart(UpdateOrderCartForm updateOrderCartForm);

    Boolean startDrive(StartDriveForm startDriveForm);

    Boolean endDrive(OrderFeeForm orderFeeForm);

    PageVo findDriverOrderPage(Page<OrderInfo> pageParam, Long driverId);

    Boolean sendOrderBillInfo(Long orderId, Long driverId);

}
