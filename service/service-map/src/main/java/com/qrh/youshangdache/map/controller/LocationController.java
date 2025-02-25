package com.qrh.youshangdache.map.controller;

import com.qrh.youshangdache.common.result.Result;
import com.qrh.youshangdache.map.service.LocationService;
import com.qrh.youshangdache.model.form.map.OrderServiceLocationForm;
import com.qrh.youshangdache.model.form.map.SearchNearByDriverForm;
import com.qrh.youshangdache.model.form.map.UpdateDriverLocationForm;
import com.qrh.youshangdache.model.form.map.UpdateOrderLocationForm;
import com.qrh.youshangdache.model.vo.map.NearByDriverVo;
import com.qrh.youshangdache.model.vo.map.OrderLocationVo;
import com.qrh.youshangdache.model.vo.map.OrderServiceLastLocationVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Tag(name = "位置API接口管理")
@RestController
@RequestMapping("/map/location")
@SuppressWarnings({"unchecked", "rawtypes"})
public class LocationController {
    @Resource
    private LocationService locationService;

    @Operation(summary = "开启接单服务：更新司机经纬度位置")
    @PostMapping("/updateDriverLocation")
    public Result<Boolean> updateDriverLocation(@RequestBody UpdateDriverLocationForm updateDriverLocationForm) {
        return Result.ok(locationService.updateDriverLocation(updateDriverLocationForm));
    }

    @Operation(summary = "关闭接单服务：删除司机经纬度位置")
    @DeleteMapping("/removeDriverLocation/{driverId}")
    public Result<Boolean> removeDriverLocation(@PathVariable Long driverId) {
        return Result.ok(locationService.removeDriverLocation(driverId));
    }

    @Operation(summary = "搜索附近满足条件的司机")
    @PostMapping("/searchNearByDriver")
    public Result<List<NearByDriverVo>> searchNearByDriver(@RequestBody SearchNearByDriverForm searchNearByDriverForm) {
        return Result.ok(locationService.searchNearByDriver(searchNearByDriverForm));
    }
    @Operation(summary = "司机赶往代驾起始点，更新订单地址到缓存")
    @PostMapping("/updateOrderLocationToCache")
    public Result<Boolean> updateOrderLocationToCache(@RequestBody UpdateOrderLocationForm  updateOrderLocationForm) {
        return Result.ok(locationService.updateOrderLocationToCache(updateOrderLocationForm));
    }

    @Operation(summary = "司机赶往代驾起始点，更新订单经纬度位置")
    @GetMapping("/getCacheOrderLocation/{orderId}")
    public Result<OrderLocationVo> getCacheOrderLocation(@PathVariable Long  orderId) {
        return Result.ok(locationService.getCacheOrderLocation(orderId));
    }

    @Operation(summary = "批量保存代驾服务订单位置")
    @PostMapping("/saveOrderServiceLocation")
    public Result<Boolean> saveOrderServiceLocation(@RequestBody List<OrderServiceLocationForm> orderServiceLocationForms) {
        return Result.ok(locationService.saveOrderServiceLocation(orderServiceLocationForms));
    }

    @Operation(summary = "代驾服务：获取订单服务最后一个位置信息")
    @GetMapping("/getOrderServiceLastLocation/{orderId}")
    public Result<OrderServiceLastLocationVo> getOrderServiceLastLocation(@PathVariable Long orderId) {
        return Result.ok(locationService.getOrderServiceLastLocation(orderId));
    }

    @Operation(summary = "代驾服务：计算订单实际里程")
    @GetMapping("/calculateOrderRealDistance/{orderId}")
    public Result<BigDecimal> calculateOrderRealDistance(@PathVariable Long orderId) {
        return Result.ok(locationService.calculateOrderRealDistance(orderId));
    }
}

