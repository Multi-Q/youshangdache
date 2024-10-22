package com.qrh.youshangdache.rules.service;

import com.atguigu.daijia.model.form.payment.ProfitsharingForm;
import com.atguigu.daijia.model.form.rules.ProfitsharingRuleRequestForm;
import com.atguigu.daijia.model.vo.rules.ProfitsharingRuleResponseVo;

public interface ProfitsharingRuleService {

    ProfitsharingRuleResponseVo calculateProfitSharingFee(ProfitsharingRuleRequestForm  profitsharingRuleRequestForm);
}
