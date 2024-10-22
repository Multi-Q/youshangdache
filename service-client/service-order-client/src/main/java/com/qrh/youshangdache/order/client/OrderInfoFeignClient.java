package com.qrh.youshangdache.order.client;

import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.model.entity.order.OrderInfo;
import com.atguigu.daijia.model.form.order.OrderInfoForm;
import com.atguigu.daijia.model.form.order.StartDriveForm;
import com.atguigu.daijia.model.form.order.UpdateOrderBillForm;
import com.atguigu.daijia.model.form.order.UpdateOrderCartForm;
import com.atguigu.daijia.model.vo.base.PageVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;


@FeignClient(value = "service-order")
public interface OrderInfoFeignClient {

    @PostMapping("/order/info/saveOrderInfo")
    Result<Long> saveOrderInfo(@RequestBody OrderInfoForm orderInfoForm);

    @GetMapping("/order/info/getOrderStatus/{orderId}")
    Result<Integer> getOrderStatus(@PathVariable("orderId") Long orderId);

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