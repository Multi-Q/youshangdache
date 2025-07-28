package com.qrh.youshangdache.dispatch.client;

import com.qrh.youshangdache.common.result.Result;
import com.qrh.youshangdache.model.vo.dispatch.NewOrderTaskVo;
import com.qrh.youshangdache.model.vo.order.NewOrderDataVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(value = "service-dispatch")
public interface NewOrderFeignClient {

    /**
     * 将任务添加到任务队列中并启动任务
     * @param newOrderDispatchVo 创建订单任务对象
     * @return 任务调度id
     */
    @PostMapping("/dispatch/newOrder/addAndStartTask")
    Result<Long> addAndStartTask(@RequestBody NewOrderTaskVo newOrderDispatchVo);


    @PostMapping("/dispatch/findNewOrderQueueData/{driverId}")
    public Result<List<NewOrderDataVo>> findNewOrderQueueData(@PathVariable Long driverId);

    @PostMapping("/dispatch/clearNewOrderQueueData/{driverId}")
    public Result<Boolean> clearNewOrderQueueData(@PathVariable Long driverId);
}