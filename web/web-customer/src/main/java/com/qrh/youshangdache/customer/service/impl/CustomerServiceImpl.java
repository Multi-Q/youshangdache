package com.qrh.youshangdache.customer.service.impl;

import com.qrh.youshangdache.common.constant.RedisConstant;
import com.qrh.youshangdache.common.execption.GuiguException;
import com.qrh.youshangdache.common.result.Result;
import com.qrh.youshangdache.common.result.ResultCodeEnum;
import com.qrh.youshangdache.customer.client.CustomerInfoFeignClient;
import com.qrh.youshangdache.customer.service.CustomerService;
import com.qrh.youshangdache.model.entity.customer.CustomerInfo;
import com.qrh.youshangdache.model.form.customer.UpdateWxPhoneForm;
import com.qrh.youshangdache.model.vo.customer.CustomerInfoVo;
import com.qrh.youshangdache.model.vo.customer.CustomerLoginVo;
import com.qrh.youshangdache.model.vo.order.CurrentOrderInfoVo;
import com.qrh.youshangdache.order.client.OrderInfoFeignClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    @Resource
    private CustomerInfoFeignClient customerInfoFeignClient;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private OrderInfoFeignClient orderInfoFeignClient;

    /**
     * 查找该用户当前订单
     * @param customerId
     * @return
     */
    @Override
    public CurrentOrderInfoVo searchCustomerCurrentOrder(Long customerId) {
        return orderInfoFeignClient.searchCustomerCurrentOrder(customerId).getData();
    }

    /**
     * 更新用户手机号码
     * @param updateWxPhoneForm
     * @return
     */
    @Override
    public Boolean updateWxPhoneNumber(UpdateWxPhoneForm updateWxPhoneForm) {
        return customerInfoFeignClient.updateWxPhoneNumber(updateWxPhoneForm).getData();
    }

    /**
     * 获取用户的登录信息
     * @param customerId 用户id
     * @return
     */
    @Override
    public CustomerLoginVo getCustomerLoginInfo(Long customerId) {
        return customerInfoFeignClient.getCustomerInfo(customerId).getData();
    }

    /**
     * 登录
     * @param code
     * @return
     */
    @Override
    public String login(String code) {
        //5生成token字符串
        String token = UUID.randomUUID().toString().replace("-", "");
        //6把用户id方法放到redis，并设置过期时间
        stringRedisTemplate.opsForValue()
                .set(RedisConstant.USER_LOGIN_KEY_PREFIX + token,
                        customerInfoFeignClient.login(code).getData().toString(),
                        RedisConstant.USER_LOGIN_KEY_TIMEOUT,
                        TimeUnit.SECONDS);
        //返回token
        return token;
    }
}
