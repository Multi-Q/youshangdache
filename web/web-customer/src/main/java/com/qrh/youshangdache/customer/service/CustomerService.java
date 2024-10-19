package com.qrh.youshangdache.customer.service;

import com.atguigu.daijia.model.form.customer.UpdateWxPhoneForm;
import com.atguigu.daijia.model.vo.customer.CustomerLoginVo;
import com.atguigu.daijia.model.vo.order.CurrentOrderInfoVo;

public interface CustomerService {

    /**
     * 小程序登录接口-用户端
     * @param code
     * @return
     */
    String login(String code);

    /**
     * 获取用户的token
     * @param customerId
     * @return
     */
    CustomerLoginVo getCustomerLoginInfo(Long customerId);

    /**
     * 更新用户手机号
     * @param updateWxPhoneForm
     * @return
     */
    Boolean updateWxPhoneNumber(UpdateWxPhoneForm updateWxPhoneForm);

    CurrentOrderInfoVo searchCustomerCurrentOrder(Long customerId);
}
