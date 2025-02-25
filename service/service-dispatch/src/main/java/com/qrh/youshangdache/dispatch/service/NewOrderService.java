package com.qrh.youshangdache.dispatch.service;

import com.qrh.youshangdache.model.vo.dispatch.NewOrderTaskVo;
import com.qrh.youshangdache.model.vo.order.NewOrderDataVo;

import java.util.List;

public interface NewOrderService {
    Long addAndStartTask(NewOrderTaskVo newOrderTaskVo);

    Boolean executeTask(Long jobId);

    Boolean clearNewOrderQueueData(Long driverId);

    List<NewOrderDataVo> findNewOrderQueueData(Long driverId);
}
