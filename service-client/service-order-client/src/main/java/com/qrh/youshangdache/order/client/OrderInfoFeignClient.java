package com.qrh.youshangdache.order.client;


import com.qrh.youshangdache.common.result.Result;
import com.qrh.youshangdache.model.entity.order.OrderInfo;
import com.qrh.youshangdache.model.form.order.OrderInfoForm;
import com.qrh.youshangdache.model.form.order.StartDriveForm;
import com.qrh.youshangdache.model.form.order.UpdateOrderBillForm;
import com.qrh.youshangdache.model.form.order.UpdateOrderCartForm;
import com.qrh.youshangdache.model.vo.base.PageVo;
import com.qrh.youshangdache.model.vo.order.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;


@FeignClient(value = "service-order")
public interface OrderInfoFeignClient {
    /**
     * 保存订单信息
     *
     * @param orderInfoForm 订单信息对象
     * @return 订单id
     */
    @PostMapping("/order/info/saveOrderInfo")
    Result<Long> saveOrderInfo(@RequestBody OrderInfoForm orderInfoForm);

    /**
     * 乘客下完单后，订单状态为1（等待接单），乘客端小程序会轮询订单状态，当订单状态为2（司机已接单）时，说明已经有司机接单了，那么页面进行跳转，进行下一步操作
     *
     * @param orderId 订单id
     * @return 订单状态代号
     */
    @GetMapping("/order/info/getOrderStatus/{orderId}")
    Result<Integer> getOrderStatus(@PathVariable("orderId") Long orderId);

    /**
     * 乘客如果已经下过单了，而且这个订单在执行中，没有结束，
     * 那么乘客是不可以再下单的，页面会弹出层，进入执行中的订单。
     *
     * @param customerId 用户id
     * @return 当前用户正在进行的订单信息
     */
    @GetMapping("/order/info/searchCustomerCurrentOrder/{customerId}")
    public Result<CurrentOrderInfoVo> searchCustomerCurrentOrder(@PathVariable Long customerId);

    @GetMapping("/order/info/searchDriverCurrentOrder/{driverId}")
    public Result<CurrentOrderInfoVo> searchDriverCurrentOrder(@PathVariable Long driverId);

    @GetMapping("/order/info/getOrderInfo/{orderId}")
    public Result<OrderInfo> getOrderInfoByOrderId(@PathVariable Long orderId);

    @GetMapping("/order/info/driverArriveStartLocation/{orderId}/{driverId}")
    public Result<Boolean> driverArriveStartLocation(@PathVariable Long orderId, @PathVariable Long driverId);

    @PostMapping("/order/info/updateOrderCart")
    public Result<Boolean> updateOrderCart(@RequestBody UpdateOrderCartForm updateOrderCartForm);

    @PostMapping("/order/info/startDrive")
    public Result<Boolean> startDrive(@RequestBody StartDriveForm startDriveForm);

    @GetMapping("/order/info/getOrderNumByTime/{startTime}/{endTime}")
    public Result<Long> getOrderNumByTime(@PathVariable String startTime, @PathVariable String endTime);

    @PostMapping("/order/info/endDrive")
    public Result<Boolean> endDrive(@RequestBody UpdateOrderBillForm updateOrderBillForm);

    @GetMapping("/order/info/findCustomerOrderPage/{customerId}/{page}/{limit}")
    public Result<PageVo> findCustomerOrderPage(@PathVariable Long customerId,
                                                @PathVariable Long limit,
                                                @PathVariable Long page);

    @GetMapping("/order/info/findDriverOrderPage/{driverId}/{page}/{limit}")
    public Result<PageVo> findDriverOrderPage(@PathVariable Long driverId,
                                              @PathVariable Long limit,
                                              @PathVariable Long page);

    @GetMapping("/order/info/getOrderBillInfo/{orderId}")
    public Result<OrderBillVo> getOrderBillInfo(@PathVariable Long orderId);

    @GetMapping("/order/info/getOrderProfitsharing/{orderId}")
    public Result<OrderProfitsharingVo> getOrderProfitsharing(@PathVariable Long orderId);

    @GetMapping("/order/info/sendOrderBillInfo/{orderId}/{driverId}")
    public Result<Boolean> sendOrderBillInfo(@PathVariable Long orderId, @PathVariable Long driverId);

    @GetMapping("/order/info/getOrderPayVo/{orderNo}/{customerId}")
    public Result<OrderPayVo> getOrderPayVo(@PathVariable String orderNo, @PathVariable Long customerId);

    @GetMapping("/order/info/updateOrderPayStatus/{orderNo} ")
    public Result<Boolean> updateOrderPayStatus(@PathVariable String orderNo);

    @GetMapping("/order/info/getOrderRewardFee/{orderNo} ")
    public Result<OrderRewardVo> getOrderRewardFee(@PathVariable String orderNo);

    @GetMapping("/order/info/updateCouponAmount/{orderId}/{couponAmount}")
    public Result<Boolean> updateCouponAmount(@PathVariable Long orderId, @PathVariable BigDecimal couponAmount);


}