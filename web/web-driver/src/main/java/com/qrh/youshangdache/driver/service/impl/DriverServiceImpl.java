package com.qrh.youshangdache.driver.service.impl;

import com.qrh.youshangdache.common.constant.RedisConstant;
import com.qrh.youshangdache.common.execption.GuiguException;
import com.qrh.youshangdache.common.result.ResultCodeEnum;
import com.qrh.youshangdache.dispatch.client.NewOrderFeignClient;
import com.qrh.youshangdache.driver.client.DriverInfoFeignClient;
import com.qrh.youshangdache.driver.service.DriverService;
import com.qrh.youshangdache.map.client.LocationFeignClient;
import com.qrh.youshangdache.model.enums.AuthStatusEnum;
import com.qrh.youshangdache.model.enums.DriverServiceStatusEnum;
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
     * @return true
     */
    @Override
    public Boolean stopService(Long driverId) {
        //更新司机的接单状态
        driverInfoFeignClient.updateServiceStatus(driverId, DriverServiceStatusEnum.DRIVER_NOT_SERVICE.getServiceStatus());
        //删除司机的位置信息
        locationFeignClient.removeDriverLocation(driverId);
        //清空司机临时队列数据
        newOrderFeignClient.clearNewOrderQueueData(driverId);
        return true;
    }

    /**
     * 更新司机的接单状态为开启接单状态
     *<p>
     *     司机完成了当日人脸认证后，开启接单，然后删除司机在redis中的位置，及清空司机的临时订单列表数据
     *</p>
     * @param driverId 司机id
     * @return true
     */
    @Override
    public Boolean startService(Long driverId) {
        //判断是否完成了验证
        DriverLoginVo driverLoginVo = driverInfoFeignClient.getDriverLoginInfo(driverId).getData();
        if (!driverLoginVo.getAuthStatus().equals(AuthStatusEnum.AUTHENTICATION_PASSED.getStatus())) {
            throw new GuiguException(ResultCodeEnum.DRIVER_NOT_AUTH);
        }
        //判断当日是否人脸识别
        Boolean isFaceRecognition = driverInfoFeignClient.isFaceRecognition(driverId).getData();
        if (!isFaceRecognition) {
            throw new GuiguException(ResultCodeEnum.DRIVER_NOT_FACIAL_RECOGNITION);
        }
        //更新司机服务状态
        driverInfoFeignClient.updateServiceStatus(driverId, DriverServiceStatusEnum.DRIVER_START_SERVICE.getServiceStatus());
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
    /**
     * 判断司机当日是否进行过人脸识别
     *
     * @return true当日已进行过人脸识别 | false当日未进行人脸识别
     */
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
