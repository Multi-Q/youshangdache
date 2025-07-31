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
     *
     * @param orderId 订单id
     * @return 订单状态代号
     */
    Integer getOrderStatus(Long orderId);

    /**
     * 查询司机的最新订单数据
     *
     * @param driverId 司机id
     * @return
     */
    List<NewOrderDataVo> findNewOrderQueueData(Long driverId);
    /**
     * 查找司机端当前订单
     *
     * <p>
     * 司机只要有执行中的订单，没有结束，那么司机是不可以接单的，页面会弹出层，进入执行中的订单
     * </p>
     *
     * @return 司机当前正在执行的订单数据
     */
    CurrentOrderInfoVo searchDriverCurrentOrder(Long driverId);

    OrderInfoVo getOrderInfoByOrderId(Long orderId, Long driverId);

    DrivingLineVo calculateDrivingLine(CalculateDrivingLineForm calculateDrivingLineForm);

    Boolean driverArriveStartLocation(Long orderId, Long driverId);

    Boolean updateOrderCart(UpdateOrderCartForm updateOrderCartForm);

    Boolean startDrive(StartDriveForm startDriveForm);

    Boolean endDrive(OrderFeeForm orderFeeForm);

    PageVo findDriverOrderPage(Page<OrderInfo> pageParam, Long driverId);

    Boolean sendOrderBillInfo(Long orderId, Long driverId);
    /**
     * 司机抢单
     *
     * <p>
     * 当前司机已经开启接单服务了，实时轮流司机服务器端临时队列，只要有合适的新订单产生，那么就会轮回获取新订单数据，进行语音播放，
     * 如果司机对这个订单感兴趣就可以抢单。注意：同一个新订单会放入满足条件的所有司机的临时队列，谁先抢到就是谁的。
     * </p>
     *
     * @param driverId 司机id
     * @param orderId  订单id
     * @return true抢单成功，否则抛出订单不存在或抢单失败异常
     */
    Boolean robNewOrder(Long driverId, Long orderId);
}
