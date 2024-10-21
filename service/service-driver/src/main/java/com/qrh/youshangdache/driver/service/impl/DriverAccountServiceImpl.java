package com.qrh.youshangdache.driver.service.impl;

import com.qrh.youshangdache.driver.mapper.DriverAccountDetailMapper;
import com.qrh.youshangdache.driver.mapper.DriverAccountMapper;
import com.qrh.youshangdache.driver.service.DriverAccountService;
import com.qrh.youshangdache.model.entity.driver.DriverAccount;
import com.qrh.youshangdache.model.entity.driver.DriverAccountDetail;
import com.qrh.youshangdache.model.form.driver.TransferForm;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class DriverAccountServiceImpl extends ServiceImpl<DriverAccountMapper, DriverAccount> implements DriverAccountService {
    @Resource
    private DriverAccountMapper driverAccountMapper;
    @Resource
    private DriverAccountDetailMapper driverAccountDetailMapper;


    @Override
    public Boolean transfer(TransferForm transferForm) {
        Long count = driverAccountDetailMapper.selectCount(new LambdaQueryWrapper<DriverAccountDetail>().eq(DriverAccountDetail::getTradeNo, transferForm.getTradeNo()));
        if (count > 0) {
            return true;
        }
        driverAccountMapper.add(transferForm.getDriverId(), transferForm.getAmount());

        DriverAccountDetail driverAccountDetail = new DriverAccountDetail();
        BeanUtils.copyProperties(transferForm, driverAccountDetail);
        driverAccountDetailMapper.insert(driverAccountDetail);
        return true;
    }
}
