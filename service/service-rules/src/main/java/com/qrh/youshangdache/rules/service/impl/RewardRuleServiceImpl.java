package com.qrh.youshangdache.rules.service.impl;

import com.atguigu.daijia.model.form.rules.RewardRuleRequest;
import com.atguigu.daijia.model.form.rules.RewardRuleRequestForm;
import com.atguigu.daijia.model.vo.rules.RewardRuleResponse;
import com.atguigu.daijia.model.vo.rules.RewardRuleResponseVo;
import com.qrh.youshangdache.rules.service.RewardRuleService;
import com.qrh.youshangdache.rules.utils.DroolsHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class RewardRuleServiceImpl implements RewardRuleService {
    private static final String RULES_CUSTOMER_RULES_DRL = "rules/RewardRule.drl";

    @Override
    public RewardRuleResponseVo calculateOrderRewardFee(RewardRuleRequestForm rewardRuleRequestForm) {
        RewardRuleRequest rewardRuleRequest = new RewardRuleRequest();
        rewardRuleRequest.setOrderNum(rewardRuleRequestForm.getOrderNum());
        //创建规则引擎对象
        KieSession kieSession = DroolsHelper.loadForRule(RULES_CUSTOMER_RULES_DRL);
        RewardRuleResponse response = new RewardRuleResponse();
        kieSession.setGlobal("rewardRuleResponse", response);
        //触发规则
        kieSession.insert(rewardRuleRequest);
        kieSession.fireAllRules();
        kieSession.dispose();
        RewardRuleResponseVo rewardRuleResponseVo = new RewardRuleResponseVo();
        rewardRuleResponseVo.setRewardAmount(response.getRewardAmount());
        return rewardRuleResponseVo;
    }
}
