package com.qrh.youshangdache.customer.controller;

import com.qrh.youshangdache.common.login.Login;
import com.qrh.youshangdache.common.result.Result;
import com.qrh.youshangdache.common.util.AuthContextHolder;
import com.qrh.youshangdache.customer.service.OrderService;
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
import com.qrh.youshangdache.model.vo.order.CurrentOrderInfoVo;
import com.qrh.youshangdache.model.vo.order.OrderInfoVo;
import com.qrh.youshangdache.model.vo.payment.WxPrepayVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "订单API接口管理")
@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    private OrderService orderService;

    @Operation(summary = "查找乘客端当前订单")
    @Login
    @GetMapping("/searchCustomerCurrentOrder")
    public Result<CurrentOrderInfoVo> searchCustomerCurrentOrder() {
        CurrentOrderInfoVo currentOrderInfoVo = new CurrentOrderInfoVo();
        currentOrderInfoVo.setIsHasCurrentOrder(false);
        return Result.ok(currentOrderInfoVo);
    }


    @Operation(summary = "预估订单数据")
    @Login
    @PostMapping("/expectOrder")
    public Result<ExpectOrderVo> expectOrder(@RequestBody ExpectOrderForm expectOrderForm) {
        return Result.ok(orderService.expectOrder(expectOrderForm));
    }

    /**
     * 乘客提交打车订单
     * @param submitOrderForm 订单信息对象
     * @return 订单号
     */
    @Operation(summary = "乘客下单")
    @Login
    @PostMapping("/submitOrder")
    public Result<Long> submitOrder(@RequestBody SubmitOrderForm submitOrderForm) {
        submitOrderForm.setCustomerId(AuthContextHolder.getUserId());
        return Result.ok(orderService.submitOrder(submitOrderForm));
    }
    /**
     * 乘客下完单后，订单状态为1，乘客端小程序会轮询订单状态，当订单状态为2时，说明已经有司机接单了，那么页面进行跳转，进行下一步操作
     * @param orderId 订单id
     * @return 订单状态代号
     */
    @Operation(summary = "查询订单状态")
    @Login
    @GetMapping("/getOrderStatus/{orderId}")
    public Result<Integer> getOrderStatus(@PathVariable Long orderId) {
        return Result.ok(orderService.getOrderStatus(orderId));
    }

    @Operation(summary = "根据订单id得到订单信息")
    @GetMapping("/getOrderInfo/{orderId}")
    @Login
    public Result<OrderInfoVo> getOrderInfoByOrderId(@PathVariable Long orderId) {
        return Result.ok(orderService.getOrderInfoByOrderId(orderId, AuthContextHolder.getUserId()));
    }

    @Operation(summary = "根据订单id获取司机基本信息")
    @Login
    @GetMapping("/getDriverInfo/{orderId}")
    public Result<DriverInfoVo> getDriverInfo(@PathVariable Long orderId) {
        return Result.ok(orderService.getDriverInfo(orderId, AuthContextHolder.getUserId()));
    }

    @Operation(summary = "司机赶往代驾起始点，更新订单经纬度位置")
    @Login
    @GetMapping("/getCacheOrderLocation/{orderId}")
    public Result<OrderLocationVo> getCacheOrderLocation(@PathVariable Long orderId) {
        return Result.ok(orderService.getCacheOrderLocation(orderId));
    }

    @Operation(summary = "计算最佳驾驶路线")
    @Login
    @GetMapping("/calculateDriverLine")
    public Result<DrivingLineVo> calculateDriverLine(@RequestBody CalculateDrivingLineForm calculateDrivingLineForm) {
        return Result.ok(orderService.calculateDriverLine(calculateDrivingLineForm));
    }

    @Operation(summary = "代驾服务：获取订单服务最后一个位置信息")
    @Login
    @GetMapping("/getOrderServiceLastLocation/{orderId}")
    public Result<OrderServiceLastLocationVo> getOrderServiceLastLocation(@PathVariable Long orderId) {
        return Result.ok(orderService.getOrderServiceLastLocation(orderId));
    }

    @Operation(summary = "获取乘客订单分页列表")
    @Login
    @GetMapping("/findCustomerOrderPage /{page}/{limit}")
    public Result<PageVo> findCustomerOrderPage(@PathVariable Long limit,
                                                @PathVariable Long page) {
        Page<OrderInfo> pageParam = new Page<>(page, limit);
        PageVo pageVo = orderService.findCustomerOrderPage(pageParam, AuthContextHolder.getUserId());
        pageVo.setPage(page);
        pageVo.setLimit(limit);
        return Result.ok(pageVo);
    }

    @Operation(summary = "创建微信支付")
    @Login
    @PostMapping("/createWxPayment")
    public Result<WxPrepayVo> createWxPayment(@RequestBody CreateWxPaymentForm createWxPaymentForm) {
        Long customerId = AuthContextHolder.getUserId();
        createWxPaymentForm.setCustomerId(customerId);
        return Result.ok(orderService.createWxPayment(createWxPaymentForm));
    }

    @Operation(summary = "支付状态查询")
    @Login
    @GetMapping("/queryPayStatus/{orderNo}")
    public Result<Boolean> queryPayStatus(@PathVariable String orderNo) {
        return Result.ok(orderService.queryPayStatus(orderNo));
    }
}

