package com.qrh.youshangdache.driver.client;

import com.qrh.youshangdache.common.result.Result;
import com.qrh.youshangdache.model.entity.driver.DriverSet;
import com.qrh.youshangdache.model.form.driver.DriverFaceModelForm;
import com.qrh.youshangdache.model.form.driver.UpdateDriverAuthInfoForm;
import com.qrh.youshangdache.model.vo.driver.DriverAuthInfoVo;
import com.qrh.youshangdache.model.vo.driver.DriverInfoVo;
import com.qrh.youshangdache.model.vo.driver.DriverLoginVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "service-driver")
public interface DriverInfoFeignClient {

    /**
     * 司机端-小程序授权登录
     *
     * @param code 微信临时票据
     * @return userID
     */
    @GetMapping(value = "/driver/info/login/{code}")
    public Result<Long> login(@PathVariable String code);

    /**
     * 司机端-获取登录后的司机信息
     *
     * @param driverId 司机id
     * @return 司机登录后的司机基本信息
     */
    @GetMapping(value = "/driver/info/getDriverLoginInfo/{driverId}")
    public Result<DriverLoginVo> getDriverLoginInfo(@PathVariable Long driverId);

    @GetMapping("/driver/info/getDriverAuthInfo/{driverId}")
    Result<DriverAuthInfoVo> getDriverAuthInfo(@PathVariable("driverId") Long driverId);

    @PostMapping("/driver/info/updateDriverAuthInfo")
    Result<Boolean> UpdateDriverAuthInfo(@RequestBody UpdateDriverAuthInfoForm updateDriverAuthInfoForm);

    @PostMapping("/driver/info/creatDriverFaceModel")
    Result<Boolean> creatDriverFaceModel(@RequestBody DriverFaceModelForm driverFaceModelForm);
    /**
     * 获取司机设置信息
     * @param driverId 司机id
     * @return 司机的设置信息
     */
    @PostMapping("/driver/info/getDriverSet/{driverId}")
    public Result<DriverSet> getDriverSettingInfo(@PathVariable Long driverId);

    @PostMapping("/driver/info/isFaceRecognition/{driverId}")
    public Result<Boolean> isFaceRecognition(@PathVariable Long driverId);

    @PostMapping("/driver/info/verifyDriverFace")
    public Result<Boolean> verifyDriverFace(@RequestBody DriverFaceModelForm driverFaceModelForm);

    @PostMapping("/driver/info/updateServiceStatus/{driverId}/{status}")
    public Result<Boolean> updateServiceStatus(@PathVariable Long driverId, @PathVariable Integer status);

    @PostMapping("/driver/info/getDriverInfo/{driverId}")
    public Result<DriverInfoVo> getDriverInfo(@PathVariable Long driverId);

    @GetMapping("/driver/info/getDriverOpenId/{driverId}")
    public Result<String> getDriverOpenId(@PathVariable Long driverId);

}