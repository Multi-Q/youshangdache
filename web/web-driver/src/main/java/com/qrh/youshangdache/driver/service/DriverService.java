package com.qrh.youshangdache.driver.service;

import com.qrh.youshangdache.model.form.driver.DriverFaceModelForm;
import com.qrh.youshangdache.model.form.driver.UpdateDriverAuthInfoForm;
import com.qrh.youshangdache.model.vo.driver.DriverAuthInfoVo;
import com.qrh.youshangdache.model.vo.driver.DriverLoginVo;

public interface DriverService {

    /**
     * 司机端-小程序授权登录
     * @param code 微信临时票据
     * @return token
     */
    String login(String code);

    /**
     * 司机端-获取登录后的司机信息
     * @param driverId 司机id
     * @return 司机登录后的司机基本信息
     */
    DriverLoginVo getDriverLoginInfo(Long driverId);

    DriverAuthInfoVo getDriverAuthInfo(Long driverId);

    Boolean updateDriverAuthInfo(UpdateDriverAuthInfoForm updateDriverAuthInfoForm);

    Boolean creatDriverFaceModel(DriverFaceModelForm driverFaceModelForm);

    Boolean isFaceRecognition(Long driverId);

    Boolean verifyDriverFace(DriverFaceModelForm driverFaceModelForm);



    Boolean startService(Long driverId);

    Boolean stopService(Long driverId);
}
