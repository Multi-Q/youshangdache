package com.qrh.youshangdache.customer.service;

import com.qrh.youshangdache.model.vo.base.PageVo;
import com.qrh.youshangdache.model.vo.coupon.AvailableCouponVo;
import com.qrh.youshangdache.model.vo.coupon.NoReceiveCouponVo;
import com.qrh.youshangdache.model.vo.coupon.NoUseCouponVo;
import com.qrh.youshangdache.model.vo.coupon.UsedCouponVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

public interface CouponService    {


    PageVo<NoReceiveCouponVo> findNoReceivePage(Long customerId, Long page, Long limit);

    PageVo<NoUseCouponVo> findNoUsePage(Long customerId, Long page, Long limit);

    PageVo<UsedCouponVo> findUsedPage(Long customerId, Long page, Long limit);

    Boolean receive(Long customerId, Long couponId);

    List<AvailableCouponVo> findAvailableCoupon(Long customerId, Long orderId);
}
