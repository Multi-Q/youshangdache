package com.qrh.youshangdache.rules.service;

import com.qrh.youshangdache.model.form.rules.FeeRuleRequestForm;
import com.qrh.youshangdache.model.vo.rules.FeeRuleResponseVo;

public interface FeeRuleService {

    FeeRuleResponseVo calculateOrderFee(FeeRuleRequestForm calculateOrderFeeForm);


}
