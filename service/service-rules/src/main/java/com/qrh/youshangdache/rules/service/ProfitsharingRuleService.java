package com.qrh.youshangdache.rules.service;

import com.qrh.youshangdache.model.form.payment.ProfitsharingForm;
import com.qrh.youshangdache.model.form.rules.ProfitsharingRuleRequestForm;
import com.qrh.youshangdache.model.vo.rules.ProfitsharingRuleResponseVo;

public interface ProfitsharingRuleService {

    ProfitsharingRuleResponseVo calculateProfitSharingFee(ProfitsharingRuleRequestForm  profitsharingRuleRequestForm);
}
