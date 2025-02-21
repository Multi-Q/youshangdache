package com.qrh.youshangdache.customer.service;

import com.qrh.youshangdache.model.entity.customer.CustomerInfo;
import com.qrh.youshangdache.model.form.customer.UpdateWxPhoneForm;
import com.qrh.youshangdache.model.vo.customer.CustomerLoginVo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CustomerInfoService extends IService<CustomerInfo> {

    /**
     * 微信小程序登录
     * @param code
     * @return
     */
    Long login(String code);

    /**
     * 获取登录后的用户信息
     * @param customerId
     * @return
     */
    CustomerLoginVo getCustomerInfo(Long customerId);

    /**
     * 更新用户手机号
     * @param updateWxPhoneForm
     * @return
     */
    Boolean updateWxPhoneNumber(UpdateWxPhoneForm updateWxPhoneForm);

    String getCustomerOpenId(Long customerId);
}
