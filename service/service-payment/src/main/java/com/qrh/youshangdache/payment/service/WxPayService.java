package com.qrh.youshangdache.payment.service;

import com.qrh.youshangdache.model.form.payment.PaymentInfoForm;
import com.qrh.youshangdache.model.vo.payment.WxPrepayVo;
import jakarta.servlet.http.HttpServletRequest;

public interface WxPayService {


    WxPrepayVo createWxPayment(PaymentInfoForm paymentInfoForm);

    void wxnotify(HttpServletRequest request);

    Boolean queryPayStatus(String orderNo);

    void handlerOrder(String orderNo);
}
