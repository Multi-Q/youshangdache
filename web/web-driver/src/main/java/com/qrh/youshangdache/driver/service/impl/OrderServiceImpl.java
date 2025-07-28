package com.qrh.youshangdache.driver.service.impl;

import com.qrh.youshangdache.common.constant.SystemConstant;
import com.qrh.youshangdache.common.execption.GuiguException;
import com.qrh.youshangdache.common.result.ResultCodeEnum;
import com.qrh.youshangdache.common.util.LocationUtil;
import com.qrh.youshangdache.dispatch.client.NewOrderFeignClient;
import com.qrh.youshangdache.driver.service.OrderService;
import com.qrh.youshangdache.map.client.LocationFeignClient;
import com.qrh.youshangdache.map.client.MapFeignClient;
import com.qrh.youshangdache.model.entity.order.OrderInfo;
import com.qrh.youshangdache.model.enums.OrderStatus;
import com.qrh.youshangdache.model.form.map.CalculateDrivingLineForm;
import com.qrh.youshangdache.model.form.order.OrderFeeForm;
import com.qrh.youshangdache.model.form.order.StartDriveForm;
import com.qrh.youshangdache.model.form.order.UpdateOrderBillForm;
import com.qrh.youshangdache.model.form.order.UpdateOrderCartForm;
import com.qrh.youshangdache.model.form.rules.FeeRuleRequestForm;
import com.qrh.youshangdache.model.form.rules.ProfitsharingRuleRequestForm;
import com.qrh.youshangdache.model.form.rules.RewardRuleRequestForm;
import com.qrh.youshangdache.model.vo.base.PageVo;
import com.qrh.youshangdache.model.vo.map.DrivingLineVo;
import com.qrh.youshangdache.model.vo.map.OrderLocationVo;
import com.qrh.youshangdache.model.vo.map.OrderServiceLastLocationVo;
import com.qrh.youshangdache.model.vo.order.*;
import com.qrh.youshangdache.model.vo.rules.FeeRuleResponseVo;
import com.qrh.youshangdache.model.vo.rules.ProfitsharingRuleResponseVo;
import com.qrh.youshangdache.model.vo.rules.RewardRuleResponseVo;
import com.qrh.youshangdache.order.client.OrderInfoFeignClient;
import com.qrh.youshangdache.rules.client.FeeRuleFeignClient;
import com.qrh.youshangdache.rules.client.ProfitsharingRuleFeignClient;
import com.qrh.youshangdache.rules.client.RewardRuleFeignClient;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderServiceImpl implements OrderService {
    @Resource
    private OrderInfoFeignClient orderInfoFeignClient;
    @Resource
    private NewOrderFeignClient newOrderFeignClient;
    @Resource
    private MapFeignClient mapFeignClient;
    @Resource
    private LocationFeignClient locationFeignClient;
    @Resource
    private FeeRuleFeignClient feeRuleFeignClient;
    @Resource
    private RewardRuleFeignClient rewardRuleFeignClient;
    @Resource
    private ProfitsharingRuleFeignClient profitsharingRuleFeignClient;
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;


    @Override
    public Boolean sendOrderBillInfo(Long orderId, Long driverId) {
        return orderInfoFeignClient.sendOrderBillInfo(orderId, driverId).getData();
    }

    @Override
    public PageVo findDriverOrderPage(Page<OrderInfo> pageParam, Long driverId) {
        return orderInfoFeignClient.findDriverOrderPage(driverId, pageParam.getPages(), pageParam.getSize()).getData();
    }

    @Override
    @SneakyThrows
    public Boolean endDrive(OrderFeeForm orderFeeForm) {
        //1.获取订单信息
        CompletableFuture<OrderInfo> orderInfoCF = CompletableFuture.supplyAsync(() -> orderInfoFeignClient.getOrderInfoByOrderId(orderFeeForm.getOrderId()).getData(), threadPoolExecutor);

        //2.防止刷单，计算司机的经纬度与代驾的终点经纬度是否在2公里范围内
        CompletableFuture<OrderServiceLastLocationVo> orderServiceLastLocationVoCF = CompletableFuture.supplyAsync(() -> locationFeignClient.getOrderServiceLastLocation(orderFeeForm.getOrderId()).getData(), threadPoolExecutor);

        //合并
        CompletableFuture.allOf(orderInfoCF, orderServiceLastLocationVoCF).join();

        //获取数据
        OrderInfo orderInfo = orderInfoCF.get();
        OrderServiceLastLocationVo orderServiceLastLocationVo = orderServiceLastLocationVoCF.get();

        //司机的位置与代驾终点位置的距离
        double distance = LocationUtil.getDistance(
                orderInfo.getEndPointLatitude().doubleValue(),
                orderInfo.getEndPointLongitude().doubleValue(),
                orderServiceLastLocationVo.getLatitude().doubleValue(),
                orderServiceLastLocationVo.getLongitude().doubleValue()
        );
        if (distance > SystemConstant.DRIVER_START_LOCATION_DISTION) {
            throw new GuiguException(ResultCodeEnum.DRIVER_END_LOCATION_DISTION_ERROR);
        }

        //3.计算订单实际里程
        CompletableFuture<BigDecimal> realDistanceCF = CompletableFuture.supplyAsync(() -> locationFeignClient.calculateOrderRealDistance(orderFeeForm.getOrderId()).getData(), threadPoolExecutor);


        //4.计算代驾实际费用
        CompletableFuture<FeeRuleResponseVo> feeRuleResponseVoCF = realDistanceCF.thenApplyAsync((realDistance) -> {
            FeeRuleRequestForm feeRuleRequestForm = new FeeRuleRequestForm();
            feeRuleRequestForm.setDistance(realDistance);
            feeRuleRequestForm.setStartTime(orderInfo.getStartServiceTime());
            Integer waitMinute = Math.abs((int) ((orderInfo.getArriveTime().getTime() - orderInfo.getAcceptTime().getTime()) / (1000 * 60)));
            feeRuleRequestForm.setWaitMinute(waitMinute);
            FeeRuleResponseVo feeRuleResponseVo = feeRuleFeignClient.calculateOrderFee(feeRuleRequestForm).getData();
            //订单总金额 需加上 路桥费、停车费、其他费用、乘客好处费
            BigDecimal totalAmount = feeRuleResponseVo.getTotalAmount()
                    .add(orderFeeForm.getTollFee())
                    .add(orderFeeForm.getParkingFee())
                    .add(orderFeeForm.getOtherFee())
                    .add(orderInfo.getFavourFee());
            feeRuleResponseVo.setTotalAmount(totalAmount);
            return feeRuleResponseVo;
        }, threadPoolExecutor);

        //5.计算系统奖励
        //5.1.获取订单数
        CompletableFuture<Long> orderNumCF = CompletableFuture.supplyAsync(() -> {
            String startTime = new DateTime(orderInfo.getStartServiceTime()).toString("yyyy-MM-dd") + " 00:00:00";
            String endTime = new DateTime(orderInfo.getEndServiceTime()).toString("yyyy-MM-dd") + " 24:00:00";
            return orderInfoFeignClient.getOrderNumByTime(startTime, endTime).getData();
        }, threadPoolExecutor);
        //5.2.封装参数
        CompletableFuture<RewardRuleResponseVo> rewardRuleResponseVoCF = orderNumCF.thenApplyAsync((orderNum) -> {
            RewardRuleRequestForm rewardRuleRequestForm = new RewardRuleRequestForm();
            rewardRuleRequestForm.setStartTime(orderInfo.getStartServiceTime());
            rewardRuleRequestForm.setOrderNum(orderNum);
            //5.3.执行
            return rewardRuleFeignClient.calculateOrderRewardFee(rewardRuleRequestForm).getData();
        }, threadPoolExecutor);

        //6.计算分账信息
        CompletableFuture<ProfitsharingRuleResponseVo> profitsharingRuleResponseVoCF = feeRuleResponseVoCF.thenCombineAsync(
                orderNumCF,
                (feeRuleResponseVo, orderNum) -> {
                    ProfitsharingRuleRequestForm profitsharingRuleRequestForm = new ProfitsharingRuleRequestForm();
                    profitsharingRuleRequestForm.setOrderAmount(feeRuleResponseVo.getTotalAmount());
                    profitsharingRuleRequestForm.setOrderNum(orderNum);
                    return profitsharingRuleFeignClient.calculateProfitSharingFee(profitsharingRuleRequestForm).getData();
                },
                threadPoolExecutor
        );

        CompletableFuture.allOf(orderServiceLastLocationVoCF,
                realDistanceCF,
                feeRuleResponseVoCF,
                orderNumCF,
                rewardRuleResponseVoCF,
                profitsharingRuleResponseVoCF
        ).join();

        //获取执行结果
        BigDecimal realDistance = realDistanceCF.get();
        FeeRuleResponseVo feeRuleResponseVo = feeRuleResponseVoCF.get();
        RewardRuleResponseVo rewardRuleResponseVo = rewardRuleResponseVoCF.get();
        ProfitsharingRuleResponseVo profitsharingRuleResponseVo = profitsharingRuleResponseVoCF.get();

        //7.封装更新订单账单相关实体对象
        UpdateOrderBillForm updateOrderBillForm = new UpdateOrderBillForm();
        updateOrderBillForm.setOrderId(orderFeeForm.getOrderId());
        updateOrderBillForm.setDriverId(orderFeeForm.getDriverId());
        updateOrderBillForm.setTollFee(orderFeeForm.getTollFee());
        updateOrderBillForm.setParkingFee(orderFeeForm.getParkingFee());
        updateOrderBillForm.setOtherFee(orderFeeForm.getOtherFee());
        updateOrderBillForm.setFavourFee(orderInfo.getFavourFee());
        updateOrderBillForm.setRealDistance(realDistance);

        BeanUtils.copyProperties(rewardRuleResponseVo, updateOrderBillForm);
        BeanUtils.copyProperties(feeRuleResponseVo, updateOrderBillForm);
        BeanUtils.copyProperties(profitsharingRuleResponseVo, updateOrderBillForm);
        updateOrderBillForm.setProfitsharingRuleId(profitsharingRuleResponseVo.getProfitsharingRuleId());

        //8.结束代驾更新账单
        orderInfoFeignClient.endDrive(updateOrderBillForm);
        return true;
    }

    @Override
    public Boolean startDrive(StartDriveForm startDriveForm) {
        return orderInfoFeignClient.startDrive(startDriveForm).getData();
    }

    @Override
    public Boolean updateOrderCart(UpdateOrderCartForm updateOrderCartForm) {
        return orderInfoFeignClient.updateOrderCart(updateOrderCartForm).getData();
    }

    @Override
    public Boolean driverArriveStartLocation(Long orderId, Long driverId) {
        OrderInfo orderInfo = orderInfoFeignClient.getOrderInfoByOrderId(orderId).getData();
        OrderLocationVo orderLocationVo = locationFeignClient.getCacheOrderLocation(orderId).getData();
        double distance = LocationUtil.getDistance(orderInfo.getStartPointLatitude().doubleValue(),
                orderInfo.getStartPointLongitude().doubleValue(),
                orderLocationVo.getLatitude().doubleValue(),
                orderLocationVo.getLongitude().doubleValue());
        if (distance > SystemConstant.DRIVER_START_LOCATION_DISTION) {
            throw new GuiguException(ResultCodeEnum.DRIVER_START_LOCATION_DISTION_ERROR);
        }
        return orderInfoFeignClient.driverArriveStartLocation(orderId, driverId).getData();
    }

    @Override
    public DrivingLineVo calculateDrivingLine(CalculateDrivingLineForm calculateDrivingLineForm) {
        return mapFeignClient.calculateDrivingLine(calculateDrivingLineForm).getData();
    }

    @Override
    public OrderInfoVo getOrderInfoByOrderId(Long orderId, Long driverId) {
        OrderInfo orderInfo = orderInfoFeignClient.getOrderInfoByOrderId(orderId).getData();
        if (orderInfo.getCustomerId() != driverId) {
            throw new GuiguException(ResultCodeEnum.ILLEGAL_REQUEST);
        }
        OrderBillVo orderBillVo = null;
        OrderProfitsharingVo orderProfitsharingVo = null;
        if (orderInfo.getStatus() >= OrderStatus.END_SERVICE.getStatus()) {
            orderBillVo = orderInfoFeignClient.getOrderBillInfo(orderId).getData();
            orderProfitsharingVo = orderInfoFeignClient.getOrderProfitsharing(orderId).getData();
        }
        OrderInfoVo orderInfoVo = new OrderInfoVo();
        BeanUtils.copyProperties(orderInfo, orderInfoVo);
        orderInfoVo.setOrderId(orderId);
        orderInfoVo.setOrderBillVo(orderBillVo);
        orderInfoVo.setOrderProfitsharingVo(orderProfitsharingVo);
        return orderInfoVo;
    }

    @Override
    public CurrentOrderInfoVo searchDriverCurrentOrder(Long driverId) {
        return orderInfoFeignClient.searchDriverCurrentOrder(driverId).getData();
    }
    /**
     * 乘客下完单后，订单状态为1（等待接单），乘客端小程序会轮询订单状态，当订单状态为2（司机已接单）时，说明已经有司机接单了，那么页面进行跳转，进行下一步操作
     * @param orderId 订单id
     * @return 订单状态代号
     */
    @Override
    public Integer getOrderStatus(Long orderId) {
        return orderInfoFeignClient.getOrderStatus(orderId).getData();
    }

    @Override
    public List<NewOrderDataVo> findNewOrderQueueData(Long driverId) {
        return newOrderFeignClient.findNewOrderQueueData(driverId).getData();
    }
}
