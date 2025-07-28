package com.qrh.youshangdache.driver.service;

import com.qrh.youshangdache.model.entity.driver.DriverInfo;
import com.qrh.youshangdache.model.entity.driver.DriverSet;
import com.qrh.youshangdache.model.form.driver.DriverFaceModelForm;
import com.qrh.youshangdache.model.form.driver.UpdateDriverAuthInfoForm;
import com.qrh.youshangdache.model.vo.driver.DriverAuthInfoVo;
import com.qrh.youshangdache.model.vo.driver.DriverInfoVo;
import com.qrh.youshangdache.model.vo.driver.DriverLoginVo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface DriverInfoService extends IService<DriverInfo> {

    /**
     * 司机端-小程序授权登录
     * @param code 微信临时票据
     * @return 用户id
     */
    Long login(String code);
    /**
     * 司机端-获取登录后的司机信息
     * @param driverId 司机id
     * @return 司机登录后的司机基本信息
     */
    DriverLoginVo getDriverLoginInfo(Long driverId);

    DriverAuthInfoVo getDriverAuthInfo(Long driverId);

    Boolean updateDriverAuthInfo(UpdateDriverAuthInfoForm updateDriverAuthInfoForm);

    Boolean creatDriverFaceModel(DriverFaceModelForm driverFaceModelForm);

    /**
     * 获取司机设置信息
     * @param driverId 司机id
     * @return 司机的设置信息
     */
    DriverSet getDriverSet(Long driverId);

    Boolean isFaceRecognition(Long driverId);

    Boolean verifyDriverFace(DriverFaceModelForm driverFaceModelForm);

    Boolean updateServiceStatus(Long driverId, Integer status);

    DriverInfoVo getDriverInfoOrder(Long driverId);

    String getDriverOpenId(Long driverId);
}
