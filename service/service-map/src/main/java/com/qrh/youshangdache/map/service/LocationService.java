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
    /**
     * 开启接单服务：更新司机经纬度位置
     *
     * @param updateDriverLocationForm 更新司机位置对象
     * @return true
     */
    Boolean updateDriverLocation(UpdateDriverLocationForm updateDriverLocationForm);

    /**
     * 关闭接单服务：删除司机经纬度位置
     *
     * @param driverId 司机id
     * @return true
     */
    Boolean removeDriverLocation(Long driverId);

    /**
     * 司机端的小程序开启接单服务后，开始实时上传司机的定位信息到redis的GEO缓存，
     * 前面乘客已经下单，现在我们就要查找附近适合接单的司机，如果有对应的司机，那就给司机发送新订单消息。
     *
     * @param searchNearByDriverForm 附近司机
     * @return 附近司机集合
     */
    List<NearByDriverVo> searchNearByDriver(SearchNearByDriverForm searchNearByDriverForm);

    Boolean updateOrderLocationToCache(UpdateOrderLocationForm updateOrderLocationForm);

    OrderLocationVo getCacheOrderLocation(Long orderId);

    Boolean saveOrderServiceLocation(List<OrderServiceLocationForm> orderId);

    OrderServiceLastLocationVo getOrderServiceLastLocation(Long orderId);

    BigDecimal calculateOrderRealDistance(Long orderId);
}
