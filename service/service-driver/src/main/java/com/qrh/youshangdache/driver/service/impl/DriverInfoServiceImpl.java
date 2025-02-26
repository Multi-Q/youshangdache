package com.qrh.youshangdache.driver.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.qrh.youshangdache.common.constant.SystemConstant;
import com.qrh.youshangdache.common.execption.GuiguException;
import com.qrh.youshangdache.common.result.ResultCodeEnum;
import com.qrh.youshangdache.driver.config.TencentCloudProperties;
import com.qrh.youshangdache.driver.mapper.*;
import com.qrh.youshangdache.driver.mapper.*;
import com.qrh.youshangdache.driver.service.CosService;
import com.qrh.youshangdache.driver.service.DriverInfoService;
import com.qrh.youshangdache.model.entity.driver.*;
import com.qrh.youshangdache.model.form.driver.DriverFaceModelForm;
import com.qrh.youshangdache.model.form.driver.UpdateDriverAuthInfoForm;
import com.qrh.youshangdache.model.vo.driver.DriverAuthInfoVo;
import com.qrh.youshangdache.model.vo.driver.DriverInfoVo;
import com.qrh.youshangdache.model.vo.driver.DriverLoginVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.iai.v20200303.IaiClient;
import com.tencentcloudapi.iai.v20200303.models.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Service
public class DriverInfoServiceImpl extends ServiceImpl<DriverInfoMapper, DriverInfo> implements DriverInfoService {

    @Resource
    private DriverInfoMapper driverInfoMapper;
    @Resource
    private WxMaService wxMaService;
    @Resource
    private DriverSetMapper driverSetMapper;
    @Resource
    private DriverAccountMapper driverAccountMapper;
    @Resource
    private DriverLoginLogMapper driverLoginLogMapper;
    @Resource
    private CosService cosService;
    @Resource
    private TencentCloudProperties tencentCloudProperties;
    @Resource
    private DriverFaceRecognitionMapper driverFaceRecognitionMapper;

    /**
     * 获取司机openId
     *
     * @param driverId 司机id
     * @return openId
     */
    @Override
    public String getDriverOpenId(Long driverId) {
        LambdaQueryWrapper<DriverInfo> wrapper = new LambdaQueryWrapper<DriverInfo>().eq(DriverInfo::getId, driverId);
        DriverInfo driverInfo = driverInfoMapper.selectOne(wrapper);
        if (driverInfo == null) throw new GuiguException(ResultCodeEnum.ACCOUNT_NOT_EXIST);
        return driverInfo.getWxOpenId();
    }

    /**
     * 乘客端进入司乘同显页面，需要加载司机的基本信息，显示司机的姓名、头像及驾龄等信息
     *
     * @param driverId 司机id
     * @return DriverInfoVo
     */
    @Override
    public DriverInfoVo getDriverInfoOrder(Long driverId) {
        //司机基本信息
        DriverInfo driverInfo = driverInfoMapper.selectById(driverId);
        if (driverInfo == null) throw new GuiguException(ResultCodeEnum.ACCOUNT_NOT_EXIST);

        DriverInfoVo driverInfoVo = new DriverInfoVo();
        BeanUtils.copyProperties(driverInfo, driverInfoVo);

        //计算驾龄
        Date licenseIssueDate = driverInfo.getDriverLicenseIssueDate();
        LocalDate now = LocalDate.now();
        int year = Math.abs(
                Period.between(
                                licenseIssueDate.toInstant()
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate(),
                                now)
                        .getYears());
        driverInfoVo.setDriverLicenseAge(year);
        return driverInfoVo;
    }

    /**
     * 更新司机的接单状态
     * @param driverId 司机id
     * @param status 司机当前的状态
     * @return true|false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateServiceStatus(Long driverId, Integer status) {
        LambdaQueryWrapper<DriverSet> queryWrapper = new LambdaQueryWrapper<DriverSet>().eq(DriverSet::getDriverId, driverId);
        DriverSet driverSet = new DriverSet();
        driverSet.setServiceStatus(status);
        return driverSetMapper.update(driverSet, queryWrapper) > 0;
    }

    @Override
    public Boolean verifyDriverFace(DriverFaceModelForm driverFaceModelForm) {
        try {
            Credential cred = new Credential(tencentCloudProperties.getSecretId(), tencentCloudProperties.getSecretKey());
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("iai.tencentcloudapi.com");
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            IaiClient client = new IaiClient(cred,
                    tencentCloudProperties.getRegion(),
                    clientProfile);
            VerifyFaceRequest request = new VerifyFaceRequest();
            request.setImage(driverFaceModelForm.getImageBase64());
            request.setPersonId(driverFaceModelForm.getDriverId().toString());
            VerifyFaceResponse resp = client.VerifyFace(request);
            if (resp.getIsMatch()) {
                //照片比对成功,静态活体检测
                Boolean isSuccess = this.detectLiveFace(driverFaceModelForm.getImageBase64());
                if (isSuccess) {
                    DriverFaceRecognition driverFaceRecognition = new DriverFaceRecognition();
                    driverFaceRecognition.setDriverId(driverFaceModelForm.getDriverId());
                    driverFaceRecognition.setFaceDate(new Date());
                    driverFaceRecognitionMapper.insert(driverFaceRecognition);
                    return true;
                }
            }

        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
        throw new GuiguException(ResultCodeEnum.DATA_ERROR);
    }

    @Override
    public Boolean isFaceRecognition(Long driverId) {
        LambdaQueryWrapper<DriverFaceRecognition> queryWrapper = new LambdaQueryWrapper<DriverFaceRecognition>()
                .eq(DriverFaceRecognition::getDriverId, driverId)
                .eq(DriverFaceRecognition::getFaceDate, new DateTime().toString("yyyy-MM-dd"));
        Long count = driverFaceRecognitionMapper.selectCount(queryWrapper);
        return count != 0;
    }

    /**
     * 获取司机设置
     * @param driverId
     * @return
     */
    @Override
    public DriverSet getDriverSet(Long driverId) {
        return driverSetMapper.selectOne(new LambdaQueryWrapper<DriverSet>().eq(DriverSet::getDriverId, driverId));
    }

    @Override
    public Boolean creatDriverFaceModel(DriverFaceModelForm driverFaceModelForm) {
        DriverInfo driverInfo = this.getById(driverFaceModelForm.getDriverId());
        try {
            Credential cred = new Credential(tencentCloudProperties.getSecretId(), tencentCloudProperties.getSecretKey());
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("iai.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            IaiClient client = new IaiClient(cred, tencentCloudProperties.getRegion(), clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            CreatePersonRequest req = new CreatePersonRequest();
            req.setGroupId(tencentCloudProperties.getPersionGroupId());
            //基本信息
            req.setPersonId(String.valueOf(driverInfo.getId()));
            req.setGender(Long.parseLong(driverInfo.getGender()));
            req.setQualityControl(4L);
            req.setUniquePersonControl(4L);
            req.setPersonName(driverInfo.getName());
            req.setImage(driverFaceModelForm.getImageBase64());

            // 返回的resp是一个CreatePersonResponse的实例，与请求对象对应
            CreatePersonResponse resp = client.CreatePerson(req);
            // 输出json格式的字符串回包
            System.out.println(CreatePersonResponse.toJsonString(resp));
            if (StringUtils.hasText(resp.getFaceId())) {
                //人脸校验必要参数，保存到数据库表
                driverInfo.setFaceModelId(resp.getFaceId());
                this.updateById(driverInfo);
            }
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
            return false;
        }
        return true;
    }

    /**
     * 更新司机认证信息
     * @param updateDriverAuthInfoForm
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean updateDriverAuthInfo(UpdateDriverAuthInfoForm updateDriverAuthInfoForm) {
        DriverInfo driverInfo = new DriverInfo();
        driverInfo.setId(updateDriverAuthInfoForm.getDriverId());
        BeanUtils.copyProperties(updateDriverAuthInfoForm, driverInfo);
        return this.updateById(driverInfo);
    }

    /**
     * 获取司机认证信息
     * @param driverId 司机id
     * @return
     */
    @Override
    public DriverAuthInfoVo getDriverAuthInfo(Long driverId) {
        DriverInfo driverInfo = driverInfoMapper.selectById(driverId);
        if (driverInfo == null) throw new GuiguException(ResultCodeEnum.ACCOUNT_NOT_EXIST);
        DriverAuthInfoVo driverAuthInfoVo = new DriverAuthInfoVo();
        BeanUtils.copyProperties(driverInfo, driverAuthInfoVo);

        driverAuthInfoVo.setIdcardBackShowUrl(cosService.getImageUrl(driverAuthInfoVo.getIdcardBackUrl()));
        driverAuthInfoVo.setIdcardFrontShowUrl(cosService.getImageUrl(driverAuthInfoVo.getIdcardFrontUrl()));
        driverAuthInfoVo.setIdcardHandShowUrl(cosService.getImageUrl(driverAuthInfoVo.getIdcardHandUrl()));
        driverAuthInfoVo.setDriverLicenseFrontShowUrl(cosService.getImageUrl(driverAuthInfoVo.getDriverLicenseFrontUrl()));
        driverAuthInfoVo.setDriverLicenseBackShowUrl(cosService.getImageUrl(driverAuthInfoVo.getDriverLicenseBackUrl()));
        driverAuthInfoVo.setDriverLicenseHandShowUrl(cosService.getImageUrl(driverAuthInfoVo.getDriverLicenseHandUrl()));

        return driverAuthInfoVo;
    }

    /**
     * 获取登录后的司机信息
     *
     * @param driverId 司机id
     * @return 登录后的司机信息
     */
    @Override
    public DriverLoginVo getDriverLoginInfo(Long driverId) {
        //根据司机id获取司机信息
        DriverInfo driverInfo = driverInfoMapper.selectById(driverId);
        //driverInfo->DriverLoginVo
        if (driverInfo == null) throw new GuiguException(ResultCodeEnum.ACCOUNT_NOT_EXIST);
        DriverLoginVo driverLoginVo = new DriverLoginVo();
        BeanUtils.copyProperties(driverInfo, driverLoginVo);
        //是否建立人脸识别
        String faceModelId = driverInfo.getFaceModelId();
        boolean isArchiveFace = StringUtils.hasText(faceModelId);
        driverLoginVo.setIsArchiveFace(isArchiveFace);
        return driverLoginVo;
    }

    /**
     * 登录
     *
     * @param code 微信发过来的临时票据
     * @return 司机id
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Long login(String code) {
        try {
            //根据code+小程序id+秘钥请求微信接口，返回openid
            WxMaJscode2SessionResult sessionInfo = wxMaService.getUserService().getSessionInfo(code);
            String openid = sessionInfo.getOpenid();
            //根据openid查询是否第一次登录
            LambdaQueryWrapper<DriverInfo> queryWrapper = new LambdaQueryWrapper<DriverInfo>()
                    .eq(StringUtils.hasText(openid), DriverInfo::getWxOpenId, openid);
            DriverInfo driverInfo = driverInfoMapper.selectOne(queryWrapper);
            //如果是第一次登录，driverInfo应该为null
            if (driverInfo == null) {
                //添加司机基本信息
                driverInfo = new DriverInfo();
                driverInfo.setNickname("用户" + System.currentTimeMillis());
                driverInfo.setAvatarUrl("https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
                driverInfo.setWxOpenId(openid);
                driverInfoMapper.insert(driverInfo);
                //初始化司机配置
                DriverSet driverSet = new DriverSet();
                driverSet.setDriverId(driverInfo.getId());
                driverSet.setOrderDistance(new BigDecimal("0"));
                driverSet.setAcceptDistance(new BigDecimal(String.valueOf(SystemConstant.ACCEPT_DISTANCE)));
                driverSet.setIsAutoAccept(0);
                driverSetMapper.insert(driverSet);
                //设置司机账户信息
                DriverAccount driverAccount = new DriverAccount();
                driverAccount.setDriverId(driverInfo.getId());
                driverAccountMapper.insert(driverAccount);
            }
            //记录司机登录信息
            DriverLoginLog driverLoginLog = new DriverLoginLog();
            driverLoginLog.setDriverId(driverInfo.getId());
            driverLoginLog.setMsg("小程序登录");
            driverLoginLogMapper.insert(driverLoginLog);

            //返回司机的id
            return driverInfo.getId();
        } catch (WxErrorException e) {
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
    }


    private Boolean detectLiveFace(String img) {
        try {
            Credential cred = new Credential(tencentCloudProperties.getSecretId(),
                    tencentCloudProperties.getSecretKey());
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("iai.tencentcloudapi.com");
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            IaiClient client = new IaiClient(cred,
                    tencentCloudProperties.getRegion(),
                    clientProfile);
            DetectLiveFaceRequest req = new DetectLiveFaceRequest();
            req.setImage(img);
            DetectLiveFaceResponse resp = client.DetectLiveFace(req);
            if (resp.getIsLiveness()) {
                return true;
            }
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
        }
        return false;
    }
}