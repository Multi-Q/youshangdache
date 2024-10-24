package com.qrh.youshangdache.map.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.daijia.common.constant.RedisConstant;
import com.atguigu.daijia.common.constant.SystemConstant;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.util.LocationUtil;
import com.atguigu.daijia.driver.client.DriverInfoFeignClient;
import com.qrh.youshangdache.map.repository.OrderServiceLocationRepository;
import com.qrh.youshangdache.map.service.LocationService;
import com.atguigu.daijia.model.entity.driver.DriverSet;
import com.atguigu.daijia.model.entity.map.OrderServiceLocation;
import com.atguigu.daijia.model.form.map.OrderServiceLocationForm;
import com.atguigu.daijia.model.form.map.SearchNearByDriverForm;
import com.atguigu.daijia.model.form.map.UpdateDriverLocationForm;
import com.atguigu.daijia.model.form.map.UpdateOrderLocationForm;
import com.atguigu.daijia.model.vo.map.NearByDriverVo;
import com.atguigu.daijia.model.vo.map.OrderLocationVo;
import com.atguigu.daijia.model.vo.map.OrderServiceLastLocationVo;
import com.atguigu.daijia.order.client.OrderInfoFeignClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class LocationServiceImpl implements LocationService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private DriverInfoFeignClient driverInfoFeignClient;
    @Resource
    private OrderServiceLocationRepository orderServiceLocationRepository;
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private OrderInfoFeignClient orderInfoFeignClient;


    @Override
    public BigDecimal calculateOrderRealDistance(Long orderId) {
        // 根据订单id获取代驾订单位置信息，根据创建时间升序排序
        //查询mongdb
        List<OrderServiceLocation> list = orderServiceLocationRepository.findByOrderIdOrderByCreateTimeAsc(orderId);
        //返回查询订单位置信息list集合
        //把list集合便利，得到每个位置信息,计算两个地点的位置
        double realDistance = 0;
        if (!list.isEmpty()) {
            for (int i = 0, size = list.size() - 1; i < size; i++) {
                OrderServiceLocation location1 = list.get(i);
                OrderServiceLocation location2 = list.get(i + 1);
                double distance = LocationUtil.getDistance(location1.getLatitude().doubleValue(),
                        location1.getLongitude().doubleValue(),
                        location2.getLatitude().doubleValue(),
                        location2.getLongitude().doubleValue());
                realDistance += distance;
            }
        }
        //todo 为了测试，不好测试实际代驾距离，模拟数据
        if (realDistance == 0) {
            return orderInfoFeignClient.getOrderInfoByOrderId(orderId)
                    .getData()
                    .getExpectAmount()
                    .add(BigDecimal.valueOf(realDistance));
        }
        return BigDecimal.valueOf(realDistance);
    }

    @Override
    public OrderServiceLastLocationVo getOrderServiceLastLocation(Long orderId) {
        OrderServiceLocation orderServiceLocation = mongoTemplate.findOne(
                Query.query(Criteria.where("orderId").is(orderId))
                        .with(Sort.by(Sort.Order.desc("createTime")))
                        .limit(1),
                OrderServiceLocation.class
        );
        OrderServiceLastLocationVo orderServiceLastLocationVo = new OrderServiceLastLocationVo();
        BeanUtils.copyProperties(orderServiceLocation, orderServiceLastLocationVo);
        return orderServiceLastLocationVo;
    }

    @Override
    public Boolean saveOrderServiceLocation(List<OrderServiceLocationForm> orderServiceLocationForms) {
        List<OrderServiceLocation> list = new ArrayList<>();
        orderServiceLocationForms.forEach(orderServiceLocationForm -> {
            OrderServiceLocation orderServiceLocation = new OrderServiceLocation();
            BeanUtils.copyProperties(orderServiceLocationForm, orderServiceLocation);
            orderServiceLocation.setId(ObjectId.get().toString());
            orderServiceLocation.setCreateTime(new Date());
            list.add(orderServiceLocation);
        });
        orderServiceLocationRepository.saveAll(list);

        return null;
    }

    @Override
    public OrderLocationVo getCacheOrderLocation(Long orderId) {
        String key = RedisConstant.UPDATE_ORDER_LOCATION + orderId;
        OrderLocationVo orderLocationVo = JSON.parseObject(key, OrderLocationVo.class);
        return orderLocationVo;
    }

    @Override
    public Boolean updateOrderLocationToCache(UpdateOrderLocationForm updateOrderLocationForm) {
        String key = RedisConstant.UPDATE_ORDER_LOCATION + updateOrderLocationForm.getOrderId();
        OrderLocationVo orderLocationVo = new OrderLocationVo();
        orderLocationVo.setLongitude(updateOrderLocationForm.getLongitude());
        orderLocationVo.setLatitude(updateOrderLocationForm.getLatitude());
        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(orderLocationVo));

        return true;
    }

    @Override
    public List<NearByDriverVo> searchNearByDriver(SearchNearByDriverForm searchNearByDriverForm) {
        //搜索经纬度中5公里以内的司机
        Circle circle = new Circle(
                new Point(
                        searchNearByDriverForm.getLatitude().doubleValue(),
                        searchNearByDriverForm.getLongitude().doubleValue()
                ),
                new Distance(SystemConstant.NEARBY_DRIVER_RADIUS, RedisGeoCommands.DistanceUnit.KILOMETERS)
        );
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                .includeDistance()
                .includeCoordinates()
                .sortDescending();
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = stringRedisTemplate.opsForGeo().radius(RedisConstant.DRIVER_GEO_LOCATION, circle, args);
        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> content = results.getContent();
        //3.返回计算后的信息
        List<NearByDriverVo> list = new ArrayList();
        if (!content.isEmpty()) {
            Iterator<GeoResult<RedisGeoCommands.GeoLocation<String>>> iterator = content.iterator();
            while (iterator.hasNext()) {
                GeoResult<RedisGeoCommands.GeoLocation<String>> item = iterator.next();

                //司机id
                Long driverId = Long.parseLong(item.getContent().getName());
                //当前距离
                BigDecimal currentDistance = new BigDecimal(item.getDistance().getValue()).setScale(2, RoundingMode.HALF_UP);

                //获取司机接单设置参数
                DriverSet driverSet = driverInfoFeignClient.getDriverSet(driverId).getData();
                //接单里程判断，acceptDistance==0：不限制，
                if (driverSet.getAcceptDistance().doubleValue() != 0 &&
                        driverSet.getAcceptDistance().subtract(currentDistance).doubleValue() < 0) {
                    continue;
                }
                //订单里程判断，orderDistance==0：不限制
                if (driverSet.getOrderDistance().doubleValue() != 0 &&
                        driverSet.getOrderDistance()
                                .subtract(searchNearByDriverForm.getMileageDistance())
                                .doubleValue() < 0) {
                    continue;
                }

                //满足条件的附近司机信息
                NearByDriverVo nearByDriverVo = new NearByDriverVo();
                nearByDriverVo.setDriverId(driverId);
                nearByDriverVo.setDistance(currentDistance);
                list.add(nearByDriverVo);
            }
        }
        return list;
    }

    @Override
    public Boolean updateDriverLocation(UpdateDriverLocationForm updateDriverLocationForm) {
        //     Redis GEO 主要用于存储地理位置信息，并对存储的信息进行相关操作，该功能在 Redis 3.2 版本新增。
        //    后续用在，乘客下单后寻找5公里范围内开启接单服务的司机，通过Redis GEO进行计算
        Point point = new Point(updateDriverLocationForm.getLongitude().doubleValue(), updateDriverLocationForm.getLatitude().doubleValue());
        stringRedisTemplate.opsForGeo().add(RedisConstant.DRIVER_GEO_LOCATION, point, updateDriverLocationForm.getDriverId().toString());
        return true;
    }

    @Override
    public Boolean removeDriverLocation(Long driverId) {
        stringRedisTemplate.opsForGeo().remove(RedisConstant.DRIVER_GEO_LOCATION, driverId.toString());
        return true;
    }
}
