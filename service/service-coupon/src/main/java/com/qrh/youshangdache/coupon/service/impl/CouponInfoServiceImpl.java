package com.qrh.youshangdache.coupon.service.impl;

import com.qrh.youshangdache.common.constant.RedisConstant;
import com.qrh.youshangdache.common.execption.GuiguException;
import com.qrh.youshangdache.common.result.ResultCodeEnum;
import com.qrh.youshangdache.coupon.mapper.CouponInfoMapper;
import com.qrh.youshangdache.coupon.mapper.CustomerCouponMapper;
import com.qrh.youshangdache.coupon.service.CouponInfoService;
import com.qrh.youshangdache.model.entity.coupon.CouponInfo;
import com.qrh.youshangdache.model.entity.coupon.CustomerCoupon;
import com.qrh.youshangdache.model.form.coupon.UseCouponForm;
import com.qrh.youshangdache.model.vo.base.PageVo;
import com.qrh.youshangdache.model.vo.coupon.AvailableCouponVo;
import com.qrh.youshangdache.model.vo.coupon.NoReceiveCouponVo;
import com.qrh.youshangdache.model.vo.coupon.NoUseCouponVo;
import com.qrh.youshangdache.model.vo.coupon.UsedCouponVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class CouponInfoServiceImpl extends ServiceImpl<CouponInfoMapper, CouponInfo> implements CouponInfoService {
    @Resource
    private CouponInfoMapper couponInfoMapper;
    @Resource
    private CustomerCouponMapper customerCouponMapper;
    @Resource
    private RedissonClient redissonClient;



    @Override
    public BigDecimal useCoupon(UseCouponForm useCouponForm) {
        //1 根据id获取乘客优惠券信息
        CustomerCoupon customerCoupon = customerCouponMapper.selectById(useCouponForm.getCustomerCouponId());
        if (customerCoupon == null) {
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
        //2 根据优惠券id获取优惠券信息
        CouponInfo couponInfo = couponInfoMapper.selectById(customerCoupon.getCouponId());
        if (couponInfo == null) {
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
        //3 判断优惠券是否是当前乘客所持有的
        if (customerCoupon.getCustomerId() != useCouponForm.getCustomerId()) {
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
        //4 判断是否具备优惠券使用条件
        BigDecimal reduceAmount = null;
        if (couponInfo.getCouponType() == 1) {
            if (couponInfo.getConditionAmount().doubleValue() == 0 &&
                    useCouponForm.getOrderAmount().subtract(couponInfo.getAmount()).doubleValue() > 0) {
                reduceAmount = couponInfo.getAmount();
            }
            if (couponInfo.getConditionAmount().doubleValue() > 0 &&
                    useCouponForm.getOrderAmount().subtract(couponInfo.getConditionAmount()).doubleValue() > 0) {
                reduceAmount = couponInfo.getAmount();
            }
        } else if (couponInfo.getCouponType() == 2) {
            BigDecimal discountOrderAmount = useCouponForm.getOrderAmount()
                    .multiply(couponInfo.getDiscount())
                    .divide(new BigDecimal("10"))
                    .setScale(2, RoundingMode.HALF_UP);
            if (couponInfo.getConditionAmount().doubleValue() == 0) {
                reduceAmount = useCouponForm.getOrderAmount().subtract(discountOrderAmount);
            }
            if (couponInfo.getConditionAmount().doubleValue() > 0 &&
                    useCouponForm.getOrderAmount().subtract(couponInfo.getConditionAmount()).doubleValue() > 0) {
                reduceAmount = useCouponForm.getOrderAmount().subtract(discountOrderAmount);
            }
        }
        //5 如果满足条件，更新两张表的数据
        if (reduceAmount.doubleValue() > 0) {
            Integer oldUseCount = couponInfo.getUseCount();
            couponInfo.setUseCount(oldUseCount + 1);
            //更新已使用的数量
            couponInfoMapper.updateById(couponInfo);
            //更新customer_coupon
            CustomerCoupon updateCustomerCoupon = new CustomerCoupon();
            updateCustomerCoupon.setId(customerCoupon.getId());
            updateCustomerCoupon.setUsedTime(new Date());
            updateCustomerCoupon.setOrderId(useCouponForm.getOrderId());
            customerCouponMapper.updateById(updateCustomerCoupon);
            return reduceAmount;
        }
        return null;
    }

    @Override
    public List<AvailableCouponVo> findAvailableCoupon(Long customerId, BigDecimal orderAmount) {
        //1 创建一个list集合，存储返回的数据
        List<AvailableCouponVo> availableCouponVoList = new ArrayList<>();
        //2 根据乘客id，获取乘客已经领取但是没有使用的优惠券列表
        List<NoUseCouponVo> list = couponInfoMapper.findNoUseList(customerId);
        //3 遍历乘客未使用优惠券列表，得到每个优惠券
        List<NoUseCouponVo> typeList = list.stream().filter(item -> item.getCouponType() == 1).collect(Collectors.toList());
        //3 是现金券
        for (NoUseCouponVo noUseCouponVo : typeList) {
            BigDecimal reduceAmount = noUseCouponVo.getAmount();
            //没有门槛
            if (noUseCouponVo.getConditionAmount().doubleValue() == 0 && orderAmount.subtract(reduceAmount).doubleValue() > 0) {
                availableCouponVoList.add(this.buildBestNoUseCouponVo(noUseCouponVo, reduceAmount));
            }
            //有门槛
            if (noUseCouponVo.getConditionAmount().doubleValue() > 0 && orderAmount.subtract(noUseCouponVo.getConditionAmount()).doubleValue() > 0) {
                availableCouponVoList.add(this.buildBestNoUseCouponVo(noUseCouponVo, reduceAmount));
            }
        }
        //4 折扣券
        List<NoUseCouponVo> typeList2 = list.stream().filter(item -> item.getCouponType() == 2).collect(Collectors.toList());
        for (NoUseCouponVo noUseCouponVo : typeList2) {
            //折扣之后的金额
            BigDecimal discountAmount = orderAmount.multiply(noUseCouponVo.getDiscount())
                    .divide(new BigDecimal("10"))
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal reduceAmount = orderAmount.subtract(discountAmount);
            //没有门槛
            if (noUseCouponVo.getConditionAmount().doubleValue() == 0) {
                availableCouponVoList.add(this.buildBestNoUseCouponVo(noUseCouponVo, reduceAmount));
            }
            //有门槛
            if (noUseCouponVo.getConditionAmount().doubleValue() > 0 && orderAmount.subtract(noUseCouponVo.getConditionAmount()).doubleValue() > 0) {
                availableCouponVoList.add(this.buildBestNoUseCouponVo(noUseCouponVo, reduceAmount));
            }
        }
        //5 把满足条件的优惠券放到list中
        if (!CollectionUtils.isEmpty(availableCouponVoList)) {
            availableCouponVoList.sort(Comparator.comparing(AvailableCouponVo::getReduceAmount));
        }
        return availableCouponVoList;
    }

    private AvailableCouponVo buildBestNoUseCouponVo(NoUseCouponVo noUseCouponVo, BigDecimal reduceAmount) {
        AvailableCouponVo availableCouponVo = new AvailableCouponVo();
        BeanUtils.copyProperties(noUseCouponVo, availableCouponVo);
        availableCouponVo.setCouponId(noUseCouponVo.getId());
        availableCouponVo.setReduceAmount(reduceAmount);
        return availableCouponVo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean receive(Long customerId, Long couponId) {
        //1、查询优惠券
        CouponInfo couponInfo = this.getById(couponId);
        if (null == couponInfo) {
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }

        //2、优惠券过期日期判断
        if (couponInfo.getExpireTime().before(new Date())) {
            throw new GuiguException(ResultCodeEnum.COUPON_EXPIRE);
        }

        //3、校验库存，优惠券领取数量判断
        if (couponInfo.getPublishCount() != 0 && couponInfo.getReceiveCount() >= couponInfo.getPublishCount()) {
            throw new GuiguException(ResultCodeEnum.COUPON_LESS);
        }
        RLock lock = null;
        try {
            lock = redissonClient.getLock(RedisConstant.COUPON_LOCK + customerId);
            boolean flag = lock.tryLock(RedisConstant.COUPON_LOCK_WAIT_TIME, RedisConstant.COUPON_LOCK_LEASE_TIME, TimeUnit.SECONDS);
            if (flag) {
                //4、校验每人限领数量
                if (couponInfo.getPerLimit() > 0) {
                    //4.1、统计当前用户对当前优惠券的已经领取的数量
                    long count = customerCouponMapper.selectCount(new LambdaQueryWrapper<CustomerCoupon>().eq(CustomerCoupon::getCouponId, couponId).eq(CustomerCoupon::getCustomerId, customerId));
                    //4.2、校验限领数量
                    if (count >= couponInfo.getPerLimit()) {
                        throw new GuiguException(ResultCodeEnum.COUPON_USER_LIMIT);
                    }
                }

                //5、更新优惠券领取数量
                int row = couponInfoMapper.updateReceiveCount(couponId);
                if (row == 1) {
                    //6、保存领取记录
                    this.saveCustomerCoupon(customerId, couponId, couponInfo.getExpireTime());
                    return true;
                }
            }

        } catch (Exception e) {

        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
        return true;
    }

    private void saveCustomerCoupon(Long customerId, Long couponId, Date expireTime) {
        CustomerCoupon customerCoupon = new CustomerCoupon();
        customerCoupon.setCustomerId(customerId);
        customerCoupon.setCouponId(couponId);
        customerCoupon.setStatus(1);
        customerCoupon.setReceiveTime(new Date());
        customerCoupon.setExpireTime(expireTime);
        customerCouponMapper.insert(customerCoupon);
    }

    @Override
    public PageVo<UsedCouponVo> findUsedPage(Page<CouponInfo> pageParam, Long customerId) {
        IPage<UsedCouponVo> pageInfo = couponInfoMapper.findUsedPage(pageParam, customerId);
        return new PageVo(pageInfo.getRecords(), pageInfo.getPages(), pageInfo.getTotal());
    }

    @Override
    public PageVo<NoUseCouponVo> findNoUsePage(Page<CouponInfo> pageParam, Long customerId) {
        IPage<NoUseCouponVo> pageInfo = couponInfoMapper.findNoUsePage(pageParam, customerId);
        return new PageVo(pageInfo.getRecords(), pageInfo.getPages(), pageInfo.getTotal());
    }

    @Override
    public PageVo<NoReceiveCouponVo> findNoReceivePage(Page<CouponInfo> pageParam, Long customerId) {
        IPage<NoReceiveCouponVo> noReceivePage = couponInfoMapper.findNoReceivePage(pageParam, customerId);
        return new PageVo(noReceivePage.getRecords(), noReceivePage.getPages(), noReceivePage.getTotal());
    }
}
