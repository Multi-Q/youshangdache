package com.qrh.youshangdache.rules.service.impl;

import com.qrh.youshangdache.model.form.payment.ProfitsharingForm;
import com.qrh.youshangdache.model.form.rules.ProfitsharingRuleRequest;
import com.qrh.youshangdache.model.form.rules.ProfitsharingRuleRequestForm;
import com.qrh.youshangdache.model.form.rules.RewardRuleRequest;
import com.qrh.youshangdache.model.vo.rules.ProfitsharingRuleResponse;
import com.qrh.youshangdache.model.vo.rules.ProfitsharingRuleResponseVo;
import com.qrh.youshangdache.model.vo.rules.RewardRuleResponse;
import com.qrh.youshangdache.model.vo.rules.RewardRuleResponseVo;
import com.qrh.youshangdache.rules.mapper.ProfitsharingRuleMapper;
import com.qrh.youshangdache.rules.service.ProfitsharingRuleService;
import com.qrh.youshangdache.rules.utils.DroolsUtils;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class ProfitsharingRuleServiceImpl implements ProfitsharingRuleService {
    private static final String RULES_CUSTOMER_RULES_DRL = "rules/ProfitsharingRule.drl";
    @Autowired
    private ProfitsharingRuleMapper profitsharingRuleMapper;

    @Override
    public ProfitsharingRuleResponseVo calculateProfitSharingFee(ProfitsharingRuleRequestForm profitsharingRuleRequestForm) {
        ProfitsharingRuleRequest profitsharingRuleRequest = new ProfitsharingRuleRequest();
        profitsharingRuleRequest.setOrderNum(profitsharingRuleRequestForm.getOrderNum());
        profitsharingRuleRequest.setOrderAmount(profitsharingRuleRequestForm.getOrderAmount());
        //创建规则引擎对象
        KieSession kieSession = DroolsUtils.loadForRule(RULES_CUSTOMER_RULES_DRL);
        ProfitsharingRuleResponse profitsharingRuleResponse = new ProfitsharingRuleResponse();
        kieSession.setGlobal("profitsharingRuleResponse", profitsharingRuleResponse);
        //触发规则
        kieSession.insert(profitsharingRuleRequest);
        kieSession.fireAllRules();
        kieSession.dispose();
        ProfitsharingRuleResponseVo profitsharingRuleResponseVo = new ProfitsharingRuleResponseVo();
        BeanUtils.copyProperties(profitsharingRuleResponse, profitsharingRuleResponseVo);
        return profitsharingRuleResponseVo;
    }
}
