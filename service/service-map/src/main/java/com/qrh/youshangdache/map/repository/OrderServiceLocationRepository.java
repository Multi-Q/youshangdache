package com.qrh.youshangdache.map.repository;

import com.atguigu.daijia.model.entity.map.OrderServiceLocation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderServiceLocationRepository extends MongoRepository<OrderServiceLocation, String> {

    public List<OrderServiceLocation> findByOrderIdOrderByCreateTimeAsc(Long orderId);
}
