package com.qrh.youshangdache.driver.controller;

import com.qrh.youshangdache.common.result.Result;
import com.qrh.youshangdache.driver.service.DriverInfoService;
import com.qrh.youshangdache.model.entity.driver.DriverSet;
import com.qrh.youshangdache.model.form.driver.DriverFaceModelForm;
import com.qrh.youshangdache.model.form.driver.UpdateDriverAuthInfoForm;
import com.qrh.youshangdache.model.vo.driver.DriverAuthInfoVo;
import com.qrh.youshangdache.model.vo.driver.DriverInfoVo;
import com.qrh.youshangdache.model.vo.driver.DriverLoginVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "司机API接口管理")
@RestController
@RequestMapping(value = "/driver/info")
public class DriverInfoController {

    @Resource
    private DriverInfoService driverInfoService;
    /**
     * 司机端-小程序授权登录
     * @param code 微信临时票据
     * @return 用户id
     */
    @Operation(summary = "小程序授权登录")
    @GetMapping(value = "/login/{code}")
    public Result<Long> login(@PathVariable String code) {
        return Result.ok(driverInfoService.login(code));
    }
    /**
     * 司机端-获取登录后的司机信息
     * @param driverId 司机id
     * @return 司机登录后的司机基本信息
     */
    @Operation(summary = "获取登录后的司机信息")
    @GetMapping(value = "/getDriverLoginInfo/{driverId}")
    public Result<DriverLoginVo> getDriverLoginInfo(@PathVariable Long driverId) {
        return Result.ok(driverInfoService.getDriverLoginInfo(driverId));
    }

    /**
     * 获取司机认证信息
     * @param driverId 司机id
     * @return
     */
    @Operation(summary = "获取司机认证信息")
    @GetMapping(value = "/getDriverAuthInfo/{driverId}")
    public Result<DriverAuthInfoVo> getDriverAuthInfo(@PathVariable Long driverId) {
        return Result.ok(driverInfoService.getDriverAuthInfo(driverId));
    }

    @Operation(summary = "更新司机认证信息")
    @PostMapping("/updateDriverAuthInfo")
    public Result<Boolean> UpdateDriverAuthInfo(@RequestBody UpdateDriverAuthInfoForm updateDriverAuthInfoForm) {
        return Result.ok(driverInfoService.updateDriverAuthInfo(updateDriverAuthInfoForm));
    }

    @Operation(summary = "创建司机人脸模型")
    @PostMapping("/creatDriverFaceModel")
    public Result<Boolean> creatDriverFaceModel(@RequestBody DriverFaceModelForm driverFaceModelForm) {
        return Result.ok(driverInfoService.creatDriverFaceModel(driverFaceModelForm));
    }

    /**
     * 获取司机设置信息
     * @param driverId 司机id
     * @return 司机的设置信息
     */
    @Operation(summary = "获取司机设置信息")
    @PostMapping("/getDriverSet/{driverId}")
    public Result<DriverSet> getDriverSettingInfo(@PathVariable Long driverId) {
        return Result.ok(driverInfoService.getDriverSet(driverId));
    }
    /**
     * 判断司机当日是否进行过人脸识别
     *
     * @param driverId 司机id
     * @return true当日已进行过人脸识别 | false当日未进行人脸识别
     */
    @Operation(summary = "判断司机当日是否做过人脸识别")
    @PostMapping("/isFaceRecognition/{driverId}")
    public Result<Boolean> isFaceRecognition(@PathVariable Long driverId) {
        return Result.ok(driverInfoService.isFaceRecognition(driverId));
    }

    @Operation(summary = "验证司机人脸")
    @PostMapping("/verifyDriverFace")
    public Result<Boolean> verifyDriverFace(@RequestBody DriverFaceModelForm driverFaceModelForm) {
        return Result.ok(driverInfoService.verifyDriverFace(driverFaceModelForm));
    }

    /**
     * 更新司机的接单状态
     *
     * <p>
     *     司机完成当日人脸认证后，就默认司机开启接单了
     * </p>
     * @param driverId 司机id
     * @param status 司机当前的接单状态，由未接单改为开始接单
     * @return true更新司机接单状态成功 | 更新司机接单状态失败
     */
    @Operation(summary = "更新司机的接单状态")
    @PostMapping("/updateServiceStatus/{driverId}/{status}")
    public Result<Boolean> updateServiceStatus(@PathVariable Long driverId,@PathVariable Integer status) {
        return Result.ok(driverInfoService.updateServiceStatus(driverId,status));
    }

    @Operation(summary = "获取司机基本信息")
    @PostMapping("/getDriverInfo/{driverId}")
    public Result<DriverInfoVo> getDriverInfo(@PathVariable Long driverId ) {
        return Result.ok(driverInfoService.getDriverInfoOrder(driverId));
    }
    @Operation(summary = "获取客户的openId")
    @GetMapping("/getDriverOpenId/{driverId}")
    public Result<String> getDriverOpenId(@PathVariable Long driverId) {
        return Result.ok(driverInfoService.getDriverOpenId(driverId));
    }


}

