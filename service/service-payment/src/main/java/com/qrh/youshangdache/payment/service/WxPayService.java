package com.qrh.youshangdache.payment.service;

import com.atguigu.daijia.model.form.payment.PaymentInfoForm;
import com.atguigu.daijia.model.vo.payment.WxPrepayVo;
import jakarta.servlet.http.HttpServletRequest;

public interface WxPayService {


    WxPrepayVo createWxPayment(PaymentInfoForm paymentInfoForm);

    void wxnotify(HttpServletRequest request);

    Boolean queryPayStatus(String orderNo);

    void handlerOrder(String orderNo);
}
