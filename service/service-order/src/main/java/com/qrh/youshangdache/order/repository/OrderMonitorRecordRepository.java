package com.qrh.youshangdache.order.repository;


import com.qrh.youshangdache.model.entity.order.OrderMonitorRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderMonitorRecordRepository extends MongoRepository<OrderMonitorRecord, String> {

}
