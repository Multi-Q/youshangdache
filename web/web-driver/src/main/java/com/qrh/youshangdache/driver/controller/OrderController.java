package com.qrh.youshangdache.driver.controller;

import com.atguigu.daijia.common.login.Login;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.util.AuthContextHolder;
import com.qrh.youshangdache.driver.service.OrderService;
import com.atguigu.daijia.model.entity.order.OrderInfo;
import com.atguigu.daijia.model.form.map.CalculateDrivingLineForm;
import com.atguigu.daijia.model.form.order.OrderFeeForm;
import com.atguigu.daijia.model.form.order.StartDriveForm;
import com.atguigu.daijia.model.form.order.UpdateOrderCartForm;
import com.atguigu.daijia.model.vo.base.PageVo;
import com.atguigu.daijia.model.vo.map.DrivingLineVo;
import com.atguigu.daijia.model.vo.order.CurrentOrderInfoVo;
import com.atguigu.daijia.model.vo.order.NewOrderDataVo;
import com.atguigu.daijia.model.vo.order.OrderInfoVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "订单API接口管理")
@RestController
@RequestMapping("/order")
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderController {

    @Resource
    private OrderService orderService;

    @Operation(summary = "查询订单状态")
    @Login
    @GetMapping("/getOrderStatus/{orderId}")
    public Result<Integer> getOrderStatus(@PathVariable Long orderId) {
        return Result.ok(orderService.getOrderStatus(orderId));
    }

    @Operation(summary = "查询司机的最新订单数据")
    @Login
    @PostMapping("/findNewOrderQueueData/{driverId}")
    public Result<List<NewOrderDataVo>> findNewOrderQueueData(@PathVariable Long driverId) {
        return Result.ok(orderService.findNewOrderQueueData(driverId));
    }


    @Operation(summary = "司机端查找当前订单")
    @Login
    @GetMapping("/searchDriverCurrentOrder ")
    public Result<CurrentOrderInfoVo> searchDriverCurrentOrder() {
        return Result.ok(orderService.searchDriverCurrentOrder(AuthContextHolder.getUserId()));
    }

    @Operation(summary = "根据订单id得到订单信息")
    @Login
    @GetMapping("/getOrderInfo/{orderId}")
    public Result<OrderInfoVo> getOrderInfoByOrderId(@PathVariable Long orderId) {
        return Result.ok(orderService.getOrderInfoByOrderId(orderId, AuthContextHolder.getUserId()));
    }

    @Operation(summary = "计算最佳驾驶路线")
    @Login
    @GetMapping("/calculateDrivingLine")
    public Result<DrivingLineVo> calculateDrivingLine(@RequestBody CalculateDrivingLineForm calculateDrivingLineForm) {
        return Result.ok(orderService.calculateDrivingLine(calculateDrivingLineForm));
    }

    @Operation(summary = "司机到达起始点")
    @Login
    @GetMapping("/driverArriveStartLocation/{orderId}")
    public Result<Boolean> driverArriveStartLocation(@PathVariable Long orderId) {
        Long driverId = AuthContextHolder.getUserId();
        return Result.ok(orderService.driverArriveStartLocation(orderId, driverId));
    }

    @Operation(summary = "更新代驾车辆信息")
    @Login
    @PostMapping("/updateOrderCart")
    public Result<Boolean> updateOrderCart(@RequestBody UpdateOrderCartForm updateOrderCartForm) {
        updateOrderCartForm.setDriverId(AuthContextHolder.getUserId());
        return Result.ok(orderService.updateOrderCart(updateOrderCartForm));
    }

    @Operation(summary = "开始代驾服务")
    @Login
    @PostMapping("/startDrive")
    public Result<Boolean> startDrive(@RequestBody StartDriveForm startDriveForm) {
        startDriveForm.setDriverId(AuthContextHolder.getUserId());
        return Result.ok(orderService.startDrive(startDriveForm));
    }

    @Operation(summary = "结束代驾服务更新订单账单")
    @Login
    @PostMapping("/endDrive")
    public Result<Boolean> endDrive(@RequestBody OrderFeeForm orderFeeForm) {
        orderFeeForm.setDriverId(AuthContextHolder.getUserId());
        return Result.ok(orderService.endDrive(orderFeeForm));
    }

    @Operation(summary = "获取司机订单分页列表")
    @Login
    @GetMapping("/findDriverOrderPage/{page}/{limit}")
    public Result<PageVo> findDriverOrderPage(@PathVariable Long limit,
                                              @PathVariable Long page) {
        Page<OrderInfo> pageParam = new Page<>(page, limit);
        PageVo pageVo = orderService.findDriverOrderPage(pageParam, AuthContextHolder.getUserId());
        pageVo.setPage(page);
        pageVo.setLimit(limit);
        return Result.ok(pageVo);
    }

    @Operation(summary = "发送账单信息")
    @Login
    @GetMapping("/sendOrderBillInfo/{orderId}")
    public Result<Boolean> sendOrderBillInfo(@PathVariable Long orderId) {
        return Result.ok(orderService.sendOrderBillInfo(orderId, AuthContextHolder.getUserId()));
    }

}

