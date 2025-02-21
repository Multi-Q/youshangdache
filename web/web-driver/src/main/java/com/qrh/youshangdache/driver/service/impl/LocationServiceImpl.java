package com.qrh.youshangdache.driver.service.impl;

import com.qrh.youshangdache.common.execption.GuiguException;
import com.qrh.youshangdache.common.result.Result;
import com.qrh.youshangdache.common.result.ResultCodeEnum;
import com.qrh.youshangdache.driver.client.DriverInfoFeignClient;
import com.qrh.youshangdache.driver.service.LocationService;
import com.qrh.youshangdache.map.client.LocationFeignClient;
import com.qrh.youshangdache.map.client.MapFeignClient;
import com.qrh.youshangdache.model.entity.driver.DriverSet;
import com.qrh.youshangdache.model.form.map.OrderServiceLocationForm;
import com.qrh.youshangdache.model.form.map.UpdateDriverLocationForm;
import com.qrh.youshangdache.model.form.map.UpdateOrderLocationForm;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class LocationServiceImpl implements LocationService {
    @Resource
    private LocationFeignClient locationFeignClient;
    @Resource
    private DriverInfoFeignClient driverInfoFeignClient;

    @Override
    public Boolean saveOrderServiceLocation(List<OrderServiceLocationForm> orderServiceLocationForms) {
        return locationFeignClient.saveOrderServiceLocation(orderServiceLocationForms).getData();
    }

    @Override
    public Boolean updateOrderLocationToCache(UpdateOrderLocationForm updateOrderLocationForm) {
        return locationFeignClient.updateOrderLocationToCache(updateOrderLocationForm).getData();
    }

    @Override
    public Boolean updateDriverLocation(UpdateDriverLocationForm updateDriverLocationForm) {
        //根据司机id获取司机个性化设置位置
        Long driverId = updateDriverLocationForm.getDriverId();
        DriverSet driverSet = driverInfoFeignClient.getDriverSet(driverId).getData();
        //判断：如果司机开始接单，更新位置信息
        if (driverSet.getServiceStatus() == 1) {
            return locationFeignClient.updateDriverLocation(updateDriverLocationForm).getData();

        } else {
            //没有接单
            throw new GuiguException(ResultCodeEnum.NO_START_SERVICE);
        }
    }
}
