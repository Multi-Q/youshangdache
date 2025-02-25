package com.qrh.youshangdache.dispatch.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.qrh.youshangdache.common.constant.RedisConstant;
import com.qrh.youshangdache.dispatch.mapper.OrderJobMapper;
import com.qrh.youshangdache.dispatch.service.NewOrderService;
import com.qrh.youshangdache.dispatch.xxl.client.XxlJobClient;
import com.qrh.youshangdache.map.client.LocationFeignClient;
import com.qrh.youshangdache.model.entity.dispatch.OrderJob;
import com.qrh.youshangdache.model.enums.OrderStatus;
import com.qrh.youshangdache.model.form.map.SearchNearByDriverForm;
import com.qrh.youshangdache.model.vo.dispatch.NewOrderTaskVo;
import com.qrh.youshangdache.model.vo.map.NearByDriverVo;
import com.qrh.youshangdache.model.vo.order.NewOrderDataVo;
import com.qrh.youshangdache.order.client.OrderInfoFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class NewOrderServiceImpl implements NewOrderService {
    @Autowired
    private XxlJobClient xxlJobClient;

    @Autowired
    private OrderJobMapper orderJobMapper;
    @Autowired
    private LocationFeignClient locationFeignClient;
    @Autowired
    private OrderInfoFeignClient orderInfoFeignClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 当司机接单成功后，就需要清空临时队列，释放系统空间
     * @param driverId 司机id
     * @return true|false
     */
    @Override
    public Boolean clearNewOrderQueueData(Long driverId) {
        String key = RedisConstant.DRIVER_ORDER_TEMP_LIST + driverId;
        return  stringRedisTemplate.delete(key);
    }

    /**
     * 司机查找新订单队列的数据
     * @param driverId 司机id
     * @return 新订单数据对象
     */
    @Override
    public List<NewOrderDataVo> findNewOrderQueueData(Long driverId) {
        List<NewOrderDataVo> list = new ArrayList<>();
        String key = RedisConstant.DRIVER_ORDER_TEMP_LIST + driverId;
        if(stringRedisTemplate.opsForList().size(key)>0){
            String content = stringRedisTemplate.opsForList().leftPop(key);
            NewOrderDataVo newOrderDataVo = JSONObject.parseObject(content, NewOrderDataVo.class);
            list.add(newOrderDataVo);
        }
        return list;
    }

    /**
     * 执行调度任务
     * @param jobId 任务id
     * @return
     */
    @Override
    public Boolean executeTask(Long jobId) {
        //获取任务参数
        OrderJob orderJob = orderJobMapper.selectOne(new LambdaQueryWrapper<OrderJob>().eq(OrderJob::getJobId, jobId));
        if(null == orderJob) {
            return true;
        }
        NewOrderTaskVo newOrderTaskVo = JSONObject.parseObject(orderJob.getParameter(), NewOrderTaskVo.class);

        //查询订单状态，如果该订单还在接单状态，继续执行；如果不在接单状态，则停止定时调度
        Integer orderStatus = orderInfoFeignClient.getOrderStatus(newOrderTaskVo.getOrderId()).getData();
        if(orderStatus.intValue() != OrderStatus.WAITING_ACCEPT.getStatus().intValue()) {
            xxlJobClient.stopJob(jobId);
            log.info("停止任务调度: {}", JSON.toJSONString(newOrderTaskVo));
            return true;
        }

        //搜索附近满足条件的司机
        SearchNearByDriverForm searchNearByDriverForm = new SearchNearByDriverForm();
        searchNearByDriverForm.setLongitude(newOrderTaskVo.getStartPointLongitude());
        searchNearByDriverForm.setLatitude(newOrderTaskVo.getStartPointLatitude());
        searchNearByDriverForm.setMileageDistance(newOrderTaskVo.getExpectDistance());
        List<NearByDriverVo> nearByDriverVoList = locationFeignClient.searchNearByDriver(searchNearByDriverForm).getData();
        //给司机派发订单信息
        nearByDriverVoList.forEach(driver -> {
            //记录司机id，防止重复推送订单信息
            String repeatKey = RedisConstant.DRIVER_ORDER_REPEAT_LIST+newOrderTaskVo.getOrderId();
            boolean isMember = stringRedisTemplate.opsForSet().isMember(repeatKey, driver.getDriverId());
            if(!isMember) {
                //记录该订单已放入司机临时容器
                stringRedisTemplate.opsForSet().add(repeatKey, driver.getDriverId().toString());
                //过期时间：15分钟，新订单15分钟没人接单自动取消
                stringRedisTemplate.expire(repeatKey, RedisConstant.DRIVER_ORDER_REPEAT_LIST_EXPIRES_TIME, TimeUnit.MINUTES);

                NewOrderDataVo newOrderDataVo = new NewOrderDataVo();
                newOrderDataVo.setOrderId(newOrderTaskVo.getOrderId());
                newOrderDataVo.setStartLocation(newOrderTaskVo.getStartLocation());
                newOrderDataVo.setEndLocation(newOrderTaskVo.getEndLocation());
                newOrderDataVo.setExpectAmount(newOrderTaskVo.getExpectAmount());
                newOrderDataVo.setExpectDistance(newOrderTaskVo.getExpectDistance());
                newOrderDataVo.setExpectTime(newOrderTaskVo.getExpectTime());
                newOrderDataVo.setFavourFee(newOrderTaskVo.getFavourFee());
                newOrderDataVo.setDistance(driver.getDistance());
                newOrderDataVo.setCreateTime(newOrderTaskVo.getCreateTime());

                //将消息保存到司机的临时队列里面，司机接单了会定时轮询到他的临时队列获取订单消息
                String key = RedisConstant.DRIVER_ORDER_TEMP_LIST+driver.getDriverId();
                stringRedisTemplate.opsForList().leftPush(key, JSONObject.toJSONString(newOrderDataVo));
                //过期时间：1分钟，1分钟未消费，自动过期
                //注：司机端开启接单，前端每5秒（远小于1分钟）拉取1次“司机临时队列”里面的新订单消息
                stringRedisTemplate.expire(key, RedisConstant.DRIVER_ORDER_TEMP_LIST_EXPIRES_TIME, TimeUnit.MINUTES);
                log.info("该新订单信息已放入司机临时队列: {}", JSON.toJSONString(newOrderDataVo));
            }
        });
        return true;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long addAndStartTask(NewOrderTaskVo newOrderTaskVo) {
        OrderJob orderJob = orderJobMapper.selectOne(new LambdaQueryWrapper<OrderJob>().eq(OrderJob::getOrderId, newOrderTaskVo.getOrderId()));
        if(null == orderJob) {
            Long jobId = xxlJobClient.addAndStart("newOrderTaskHandler",
                    "",
                    "0 0/1 * * * ?",
                    "新订单任务,订单id："+newOrderTaskVo.getOrderId()
            );

            //记录订单与任务的关联信息
            orderJob = new OrderJob();
            orderJob.setOrderId(newOrderTaskVo.getOrderId());
            orderJob.setJobId(jobId);
            orderJob.setParameter(JSONObject.toJSONString(newOrderTaskVo));
            orderJobMapper.insert(orderJob);
        }
        return orderJob.getJobId();
    }
}



