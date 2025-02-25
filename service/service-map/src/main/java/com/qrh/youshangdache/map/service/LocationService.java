package com.qrh.youshangdache.map.service;

import com.qrh.youshangdache.model.form.map.OrderServiceLocationForm;
import com.qrh.youshangdache.model.form.map.SearchNearByDriverForm;
import com.qrh.youshangdache.model.form.map.UpdateDriverLocationForm;
import com.qrh.youshangdache.model.form.map.UpdateOrderLocationForm;
import com.qrh.youshangdache.model.vo.map.NearByDriverVo;
import com.qrh.youshangdache.model.vo.map.OrderLocationVo;
import com.qrh.youshangdache.model.vo.map.OrderServiceLastLocationVo;

import java.math.BigDecimal;
import java.util.List;

public interface LocationService {
    Boolean updateDriverLocation(UpdateDriverLocationForm updateDriverLocationForm);

    Boolean removeDriverLocation(Long driverId);

    List<NearByDriverVo> searchNearByDriver(SearchNearByDriverForm searchNearByDriverForm);

    Boolean updateOrderLocationToCache(UpdateOrderLocationForm updateOrderLocationForm);

    OrderLocationVo getCacheOrderLocation(Long orderId);

    Boolean saveOrderServiceLocation(List<OrderServiceLocationForm> orderId);

    OrderServiceLastLocationVo getOrderServiceLastLocation(Long orderId);

    BigDecimal calculateOrderRealDistance(Long orderId);
}
