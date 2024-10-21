package com.qrh.youshangdache.driver.service;

import com.qrh.youshangdache.model.entity.driver.DriverAccount;
import com.qrh.youshangdache.model.form.driver.TransferForm;
import com.baomidou.mybatisplus.extension.service.IService;

public interface DriverAccountService extends IService<DriverAccount> {


    Boolean transfer(TransferForm transferForm);
}
