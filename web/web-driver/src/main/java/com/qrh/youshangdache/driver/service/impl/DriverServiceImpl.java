package com.qrh.youshangdache.driver.service.impl;

import com.qrh.youshangdache.common.constant.RedisConstant;
import com.qrh.youshangdache.common.execption.GuiguException;
import com.qrh.youshangdache.common.result.Result;
import com.qrh.youshangdache.common.result.ResultCodeEnum;
import com.qrh.youshangdache.dispatch.client.NewOrderFeignClient;
import com.qrh.youshangdache.driver.client.DriverInfoFeignClient;
import com.qrh.youshangdache.driver.service.DriverService;
import com.qrh.youshangdache.map.client.LocationFeignClient;
import com.qrh.youshangdache.model.form.driver.DriverFaceModelForm;
import com.qrh.youshangdache.model.form.driver.UpdateDriverAuthInfoForm;
import com.qrh.youshangdache.model.vo.driver.DriverAuthInfoVo;
import com.qrh.youshangdache.model.vo.driver.DriverLoginVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class DriverServiceImpl implements DriverService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private DriverInfoFeignClient driverInfoFeignClient;
    @Resource
    private LocationFeignClient locationFeignClient;
    @Resource
    private NewOrderFeignClient newOrderFeignClient;

    /**
     * 司机接单成功后，关闭接单功能
     * @param driverId 司机id
     * @return
     */
    @Override
    public Boolean stopService(Long driverId) {
        //更新司机的接单状态
        driverInfoFeignClient.updateServiceStatus(driverId, 0);
        //删除司机的位置信息
        locationFeignClient.removeDriverLocation(driverId);
        //清空司机临时队列数据
        newOrderFeignClient.clearNewOrderQueueData(driverId);
        return true;
    }

    /**
     * 司机开始接单
     * @param driverId 司机id
     * @return
     */
    @Override
    public Boolean startService(Long driverId) {
        //判断是否完成了验证
        DriverLoginVo driverLoginVo = driverInfoFeignClient.getDriverLoginInfo(driverId).getData();
        if (driverLoginVo.getAuthStatus().intValue() != 2) {
            throw new GuiguException(ResultCodeEnum.AUTH_ERROR);
        }
        //判断当日是否人脸识别
        Boolean isFaceRecognition = driverInfoFeignClient.isFaceRecognition(driverId).getData();
        if (!isFaceRecognition) {
            throw new GuiguException(ResultCodeEnum.FACE_ERROR);
        }
        //更新订单状态
        driverInfoFeignClient.updateServiceStatus(driverId, 1);
        //删除redis的司机的位置信息
        locationFeignClient.removeDriverLocation(driverId);
        //清空司机临时订单数据
        newOrderFeignClient.clearNewOrderQueueData(driverId);
        return true;
    }


    @Override
    public Boolean verifyDriverFace(DriverFaceModelForm driverFaceModelForm) {
        return driverInfoFeignClient.verifyDriverFace(driverFaceModelForm).getData();
    }

    @Override
    public Boolean isFaceRecognition(Long driverId) {
        return driverInfoFeignClient.isFaceRecognition(driverId).getData();
    }

    @Override
    public Boolean creatDriverFaceModel(DriverFaceModelForm driverFaceModelForm) {
        return driverInfoFeignClient.creatDriverFaceModel(driverFaceModelForm).getData();
    }

    @Override
    public Boolean updateDriverAuthInfo(UpdateDriverAuthInfoForm updateDriverAuthInfoForm) {
        return driverInfoFeignClient.UpdateDriverAuthInfo(updateDriverAuthInfoForm).getData();
    }

    @Override
    public DriverAuthInfoVo getDriverAuthInfo(Long driverId) {
        return driverInfoFeignClient.getDriverAuthInfo(driverId).getData();
    }

    /**
     * 司机端-获取登录后的司机信息
     * @param driverId 司机id
     * @return 司机登录后的司机基本信息
     */
    @Override
    public DriverLoginVo getDriverLoginInfo(Long driverId) {
        return driverInfoFeignClient.getDriverLoginInfo(driverId).getData();
    }

    /**
     * 司机端-小程序授权登录
     * @param code 微信临时票据
     * @return token
     */
    @Override
    public String login(String code) {
        //token字符串
        String token = UUID.randomUUID().toString().replace("-", "");
        //放到redis，设置过期时间
        stringRedisTemplate.opsForValue()
                .set(RedisConstant.USER_LOGIN_KEY_PREFIX + token,
                        driverInfoFeignClient.login(code).getData().toString(),
                        RedisConstant.USER_LOGIN_KEY_TIMEOUT,
                        TimeUnit.SECONDS);
        return token;
    }


}
