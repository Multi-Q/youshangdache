package com.qrh.youshangdache.order.service.impl;

import com.atguigu.daijia.common.constant.RedisConstant;
import com.atguigu.daijia.common.constant.SystemConstant;
import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.model.enums.OrderStatus;
import com.atguigu.daijia.model.form.order.OrderInfoForm;
import com.atguigu.daijia.model.form.order.StartDriveForm;
import com.atguigu.daijia.model.form.order.UpdateOrderBillForm;
import com.atguigu.daijia.model.form.order.UpdateOrderCartForm;
import com.atguigu.daijia.model.vo.base.PageVo;
import com.qrh.youshangdache.order.mapper.OrderBillMapper;
import com.qrh.youshangdache.order.mapper.OrderInfoMapper;
import com.qrh.youshangdache.order.mapper.OrderProfitsharingMapper;
import com.qrh.youshangdache.order.mapper.OrderStatusLogMapper;
import com.qrh.youshangdache.order.service.OrderInfoService;
import com.qrh.youshangdache.order.service.OrderMonitorService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.util.BeanUtil;
import jakarta.annotation.Resource;
import org.redisson.Redisson;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private OrderStatusLogMapper orderStatusLogMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private OrderMonitorService orderMonitorService;
    @Resource
    private OrderBillMapper orderBillMapper;
    @Resource
    private OrderProfitsharingMapper orderProfitsharingMapper;

    @Override
    public Boolean updateCouponAmount(Long orderId, BigDecimal couponAmount) {
        orderBillMapper.updateCouponAmount(orderId,couponAmount);
        return true;
    }

    @Override
    public void orderCancel(Long orderId) {
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        if (orderInfo.getStatus() == OrderStatus.WAITING_ACCEPT.getStatus()) {
            orderInfo.setStatus(OrderStatus.CANCEL_ORDER.getStatus());
            int i = orderInfoMapper.updateById(orderInfo);
            if (i > 0) {
                //删除接单标识
                stringRedisTemplate.delete(RedisConstant.ORDER_ACCEPT_MARK);
            }
        }
    }

    @Override
    public OrderRewardVo getOrderRewardFee(String orderNo) {
        OrderInfo orderInfo = orderInfoMapper.selectOne(new LambdaQueryWrapper<OrderInfo>()
                .eq(OrderInfo::getOrderNo, orderNo)
                .select(OrderInfo::getId, OrderInfo::getDriverId));
        OrderBill orderBill = orderBillMapper.selectOne(new LambdaQueryWrapper<OrderBill>()
                .eq(OrderBill::getOrderId, orderInfo.getId())
                .select(OrderBill::getRewardFee));
        OrderRewardVo orderRewardVo = new OrderRewardVo();
        orderRewardVo.setOrderId(orderInfo.getId());
        orderRewardVo.setDriverId(orderInfo.getDriverId());
        orderRewardVo.setRewardFee(orderRewardVo.getRewardFee());
        return orderRewardVo;
    }

    @Override
    public Boolean updateOrderPayStatus(String orderNo) {
        OrderInfo orderInfo = orderInfoMapper.selectOne(new LambdaQueryWrapper<OrderInfo>().eq(OrderInfo::getOrderNo, orderNo));
        if (orderInfo != null || orderInfo.getStatus() == OrderStatus.PAID.getStatus()) {
            return true;
        }
        OrderInfo orderInfo1 = new OrderInfo();
        orderInfo1.setStatus(OrderStatus.PAID.getStatus());
        orderInfo1.setPayTime(new Date());
        int update = orderInfoMapper.update(orderInfo1, new LambdaQueryWrapper<OrderInfo>().eq(OrderInfo::getOrderNo, orderNo));
        if (update > 0) {
            return true;
        } else {
            throw new GuiguException(ResultCodeEnum.UPDATE_ERROR);
        }
    }

    @Override
    public OrderPayVo getOrderPayVo(String orderNo, Long customerId) {
        OrderPayVo orderPayVo = orderInfoMapper.selectOrderPayVo(orderNo, customerId);
        if (orderPayVo != null) {
            String content = orderPayVo.getStartLocation() + " 到 " + orderPayVo.getEndLocation();
            orderPayVo.setContent(content);
        }
        return orderPayVo;
    }

    @Override
    public Boolean sendOrderBillInfo(Long orderId, Long driverId) {
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<OrderInfo>()
                .eq(OrderInfo::getId, orderId)
                .eq(OrderInfo::getDriverId, driverId);
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setStatus(OrderStatus.UNPAID.getStatus());
        int rows = orderInfoMapper.update(orderInfo, wrapper);
        if (rows > 0) {
            return true;
        } else {
            throw new GuiguException(ResultCodeEnum.UPDATE_ERROR);
        }

    }

    @Override
    public OrderProfitsharingVo getOrderProfitsharing(Long orderId) {
        LambdaQueryWrapper<OrderProfitsharing> wrapper = new LambdaQueryWrapper<OrderProfitsharing>().eq(OrderProfitsharing::getOrderId, orderId);
        OrderProfitsharing orderProfitsharing = orderProfitsharingMapper.selectOne(wrapper);
        OrderProfitsharingVo orderProfitsharingVo = new OrderProfitsharingVo();
        BeanUtils.copyProperties(orderProfitsharing, orderProfitsharingVo);
        return orderProfitsharingVo;
    }

    @Override
    public OrderBillVo getOrderBillInfo(Long orderId) {
        LambdaQueryWrapper<OrderBill> wrapper = new LambdaQueryWrapper<OrderBill>().eq(OrderBill::getOrderId, orderId);
        OrderBill orderBill = orderBillMapper.selectOne(wrapper);
        OrderBillVo orderBillVo = new OrderBillVo();
        BeanUtils.copyProperties(orderBill, orderBillVo);
        return orderBillVo;
    }

    @Override
    public PageVo findDriverOrderPage(Page<OrderInfo> pageParam, Long driverId) {
        IPage<OrderListVo> pageInfo = orderInfoMapper.selectDriverOrderPage(pageParam, driverId);
        PageVo<OrderListVo> pageVo = new PageVo<>(pageInfo.getRecords(), pageInfo.getPages(), pageInfo.getSize());
        return pageVo;
    }

    @Override
    public PageVo findCustomerOrderPage(Page<OrderInfo> pageParam, Long customerId) {
        IPage<OrderListVo> pageInfo = orderInfoMapper.selectCustomerOrderPage(pageParam, customerId);
        PageVo<OrderListVo> pageVo = new PageVo<>(pageInfo.getRecords(), pageInfo.getPages(), pageInfo.getSize());
        return pageVo;
    }

    @Override
    public Boolean endDrive(UpdateOrderBillForm updateOrderBillForm) {
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<OrderInfo>()
                .eq(OrderInfo::getId, updateOrderBillForm.getOrderId())
                .eq(OrderInfo::getDriverId, updateOrderBillForm.getDriverId());
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setStatus(OrderStatus.END_SERVICE.getStatus());
        orderInfo.setRealAmount(updateOrderBillForm.getTotalAmount());
        orderInfo.setFavourFee(updateOrderBillForm.getFavourFee());
        orderInfo.setRealDistance(updateOrderBillForm.getRealDistance());
        orderInfo.setEndServiceTime(new Date());
        int update = orderInfoMapper.update(orderInfo, wrapper);
        if (update > 0) {
            OrderBill orderBill = new OrderBill();
            BeanUtils.copyProperties(updateOrderBillForm, orderBill);
            orderBill.setOrderId(updateOrderBillForm.getOrderId());
            orderBill.setPayAmount(updateOrderBillForm.getTotalAmount());
            orderBillMapper.insert(orderBill);

            OrderProfitsharing orderProfitsharing = new OrderProfitsharing();
            BeanUtils.copyProperties(updateOrderBillForm, orderProfitsharing);
            orderProfitsharing.setOrderId(updateOrderBillForm.getOrderId());
            orderProfitsharing.setRuleId(updateOrderBillForm.getProfitsharingRuleId());
            orderProfitsharing.setStatus(1);
            orderProfitsharingMapper.insert(orderProfitsharing);
        } else {
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
        return true;
    }

    @Override
    public Long getOrderNumByTime(String startTime, String endTime) {
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<OrderInfo>()
                .ge(OrderInfo::getStartServiceTime, startTime)
                .le(OrderInfo::getEndServiceTime, endTime);
        Long count = orderInfoMapper.selectCount(wrapper);
        return count;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean startDrive(StartDriveForm startDriveForm) {
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getId, startDriveForm.getOrderId());
        queryWrapper.eq(OrderInfo::getDriverId, startDriveForm.getDriverId());

        OrderInfo updateOrderInfo = new OrderInfo();
        updateOrderInfo.setStatus(OrderStatus.START_SERVICE.getStatus());
        updateOrderInfo.setStartServiceTime(new Date());
        //只能更新自己的订单
        int row = orderInfoMapper.update(updateOrderInfo, queryWrapper);
        if (row == 1) {
            //记录日志
            this.log(startDriveForm.getOrderId(), OrderStatus.START_SERVICE.getStatus());
        } else {
            throw new GuiguException(ResultCodeEnum.UPDATE_ERROR);
        }

        //初始化订单监控统计数据
        OrderMonitor orderMonitor = new OrderMonitor();
        orderMonitor.setOrderId(startDriveForm.getOrderId());
        orderMonitorService.saveOrderMonitor(orderMonitor);
        return true;
    }

    @Override
    public Boolean updateOrderCart(UpdateOrderCartForm updateOrderCartForm) {
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<OrderInfo>()
                .eq(OrderInfo::getId, updateOrderCartForm.getOrderId())
                .eq(OrderInfo::getDriverId, updateOrderCartForm.getDriverId());
        OrderInfo orderInfo = new OrderInfo();
        BeanUtils.copyProperties(updateOrderCartForm, orderInfo);
        orderInfo.setStatus(OrderStatus.UPDATE_CART_INFO.getStatus());
        int rows = orderInfoMapper.update(orderInfo, queryWrapper);
        if (rows > 0) {
            return true;
        } else {
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
    }

    @Override
    public Boolean driverArriveStartLocation(Long orderId, Long driverId) {
        //跟新订单状态，到达时间
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<OrderInfo>()
                .eq(OrderInfo::getId, orderId)
                .eq(OrderInfo::getDriverId, driverId);
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setStatus(OrderStatus.DRIVER_ARRIVED.getStatus());
        orderInfo.setArriveTime(new Date());

        int rows = orderInfoMapper.update(orderInfo, queryWrapper);
        if (rows > 0) {
            return true;
        } else {
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
    }

    @Override
    public CurrentOrderInfoVo searchDriverCurrentOrder(Long driverId) {
        Integer[] statusArray = {
                OrderStatus.ACCEPTED.getStatus(),
                OrderStatus.DRIVER_ARRIVED.getStatus(),
                OrderStatus.UPDATE_CART_INFO.getStatus(),
                OrderStatus.START_SERVICE.getStatus(),
                OrderStatus.END_SERVICE.getStatus(),
                OrderStatus.UNPAID.getStatus()
        };
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<OrderInfo>()
                .eq(OrderInfo::getId, driverId)
                .in(OrderInfo::getStatus, statusArray)
                .orderByDesc(OrderInfo::getId)
                .last(" limit 1");
        OrderInfo orderInfo = orderInfoMapper.selectOne(queryWrapper);
        CurrentOrderInfoVo currentOrderInfoVo = new CurrentOrderInfoVo();
        if (orderInfo != null) {
            currentOrderInfoVo.setOrderId(orderInfo.getId());
            currentOrderInfoVo.setStatus(orderInfo.getStatus());
            currentOrderInfoVo.setIsHasCurrentOrder(true);
        } else {
            currentOrderInfoVo.setIsHasCurrentOrder(false);
        }
        return currentOrderInfoVo;
    }

    @Override
    public CurrentOrderInfoVo searchCustomerCurrentOrder(Long customerId) {
        Integer[] statusArray = {
                OrderStatus.ACCEPTED.getStatus(),
                OrderStatus.DRIVER_ARRIVED.getStatus(),
                OrderStatus.UPDATE_CART_INFO.getStatus(),
                OrderStatus.START_SERVICE.getStatus(),
                OrderStatus.END_SERVICE.getStatus(),
                OrderStatus.UNPAID.getStatus()
        };
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<OrderInfo>()
                .eq(OrderInfo::getCustomerId, customerId)
                .in(OrderInfo::getStatus, statusArray)
                .orderByDesc(OrderInfo::getId)
                .last(" limit 1");
        OrderInfo orderInfo = orderInfoMapper.selectOne(queryWrapper);
        CurrentOrderInfoVo currentOrderInfoVo = new CurrentOrderInfoVo();
        if (orderInfo != null) {
            currentOrderInfoVo.setOrderId(orderInfo.getId());
            currentOrderInfoVo.setStatus(orderInfo.getStatus());
            currentOrderInfoVo.setIsHasCurrentOrder(true);
        } else {
            currentOrderInfoVo.setIsHasCurrentOrder(false);
        }
        return currentOrderInfoVo;
    }

    @Override
    public Boolean robNewOrder(Long driverId, Long orderId) {
        // 判断定是否存在
        if (!stringRedisTemplate.hasKey(RedisConstant.ORDER_ACCEPT_MARK + orderId)) {
            throw new GuiguException(ResultCodeEnum.COB_NEW_ORDER_FAIL);
        }
        //创建锁
        RLock lock = redissonClient.getLock(RedisConstant.ORDER_ACCEPT_MARK + orderId);
        try {
            if (!stringRedisTemplate.hasKey(RedisConstant.ORDER_ACCEPT_MARK + orderId)) {
                throw new GuiguException(ResultCodeEnum.COB_NEW_ORDER_FAIL);
            }
            boolean flag = lock.tryLock(RedisConstant.ROB_NEW_ORDER_LOCK_WAIT_TIME,
                    RedisConstant.ROB_NEW_ORDER_LOCK_LEASE_TIME,
                    TimeUnit.SECONDS);
            if (flag) {

                OrderInfo orderInfo = orderInfoMapper.selectOne(new LambdaQueryWrapper<OrderInfo>()
                        .eq(OrderInfo::getId, orderId));
                orderInfo.setStatus(OrderStatus.ACCEPTED.getStatus());
                orderInfo.setDriverId(driverId);
                orderInfo.setAcceptTime(new Date());
                int row = orderInfoMapper.updateById(orderInfo);
                if (row != 1) {
                    throw new GuiguException(ResultCodeEnum.COB_NEW_ORDER_FAIL);
                }
                stringRedisTemplate.delete(RedisConstant.ORDER_ACCEPT_MARK + orderId);
            }

        } catch (InterruptedException e) {
            throw new GuiguException(ResultCodeEnum.COB_NEW_ORDER_FAIL);
        } finally {
            if (lock.isLocked()) {
                lock.lock();
            }
        }
        return true;
    }

    @Override
    public Integer getOrderStatus(Long orderId) {
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<OrderInfo>()
                .eq(OrderInfo::getId, orderId)
                .select(OrderInfo::getStatus);
        OrderInfo orderInfo = orderInfoMapper.selectOne(queryWrapper);
        if (null == orderInfo) {
            //返回null，feign解析会抛出异常，给默认值，后续会用
            return OrderStatus.NULL_ORDER.getStatus();
        }
        return orderInfo.getStatus();
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public Long saveOrderInfo(OrderInfoForm orderInfoForm) {
        OrderInfo orderInfo = new OrderInfo();
        BeanUtils.copyProperties(orderInfoForm, orderInfo);
        String orderNo = UUID.randomUUID().toString().replaceAll("-", "");
        orderInfo.setStatus(OrderStatus.WAITING_ACCEPT.getStatus());
        orderInfo.setOrderNo(orderNo);
        orderInfoMapper.insert(orderInfo);
        //生成订单之后，发送到延迟队列
        this.sendDelayMessage(orderInfo.getId());
        //记录日志
        this.log(orderInfo.getId(), orderInfo.getStatus());
        //接单标识，标识不存在了说明不在等待接单状态了
        stringRedisTemplate.opsForValue()
                .set(RedisConstant.ORDER_ACCEPT_MARK,
                        "0",
                        RedisConstant.ORDER_ACCEPT_MARK_EXPIRES_TIME,
                        TimeUnit.MINUTES);
        return orderInfo.getId();
    }

    /**
     * 生成延迟订单,用redisson实现
     *
     * @param orderId 订单id
     */
    private void sendDelayMessage(Long orderId) {
        try {
            //创建队列
            RBlockingQueue<Object> blockingQueue = redissonClient.getBlockingQueue("queue_cancel");
            //把创建队列放到延迟队列里面
            RDelayedQueue<Object> delayedQueue = redissonClient.getDelayedQueue(blockingQueue);
            //设置过期时间
            delayedQueue.offer(orderId.toString(), 15, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
    }

    public void log(Long orderId, Integer status) {
        OrderStatusLog orderStatusLog = new OrderStatusLog();
        orderStatusLog.setOrderId(orderId);
        orderStatusLog.setOrderStatus(status);
        orderStatusLog.setOperateTime(new Date());
        orderStatusLogMapper.insert(orderStatusLog);
    }
}
