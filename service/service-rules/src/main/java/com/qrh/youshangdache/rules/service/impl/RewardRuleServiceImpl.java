package com.qrh.youshangdache.rules.service.impl;

import com.qrh.youshangdache.model.form.rules.RewardRuleRequest;
import com.qrh.youshangdache.model.form.rules.RewardRuleRequestForm;
import com.qrh.youshangdache.model.vo.rules.RewardRuleResponse;
import com.qrh.youshangdache.model.vo.rules.RewardRuleResponseVo;
import com.qrh.youshangdache.rules.service.RewardRuleService;
import com.qrh.youshangdache.rules.utils.DroolsUtils;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class RewardRuleServiceImpl implements RewardRuleService {
    private static final String RULES_CUSTOMER_RULES_DRL = "rules/RewardRule.drl";

    /**
     * 计算订单奖励费用
     *
     * @param rewardRuleRequestForm
     * @return
     */
    @Override
    public RewardRuleResponseVo calculateOrderRewardFee(RewardRuleRequestForm rewardRuleRequestForm) {
        RewardRuleRequest rewardRuleRequest = new RewardRuleRequest();
        rewardRuleRequest.setOrderNum(rewardRuleRequestForm.getOrderNum());

        //创建规则引擎对象
        KieSession kieSession = DroolsUtils.loadForRule(RULES_CUSTOMER_RULES_DRL);
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
