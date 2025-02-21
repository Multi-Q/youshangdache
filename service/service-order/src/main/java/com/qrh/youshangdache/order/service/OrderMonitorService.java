package com.qrh.youshangdache.order.service;

import com.qrh.youshangdache.model.entity.order.OrderMonitor;
import com.qrh.youshangdache.model.entity.order.OrderMonitorRecord;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OrderMonitorService extends IService<OrderMonitor> {

    Boolean saveOrderMonitorRecord(OrderMonitorRecord orderMonitorRecord);

    Long saveOrderMonitor(OrderMonitor orderMonitor);

    OrderMonitor getOrderMonitor(Long orderId);

    Boolean updateOrderMonitor(OrderMonitor orderMonitor);
}
