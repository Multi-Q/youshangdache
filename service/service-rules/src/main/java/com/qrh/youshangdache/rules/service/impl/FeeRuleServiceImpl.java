package com.qrh.youshangdache.rules.service.impl;

import com.alibaba.fastjson.JSON;
import com.qrh.youshangdache.model.form.rules.FeeRuleRequest;
import com.qrh.youshangdache.model.form.rules.FeeRuleRequestForm;
import com.qrh.youshangdache.model.vo.rules.FeeRuleResponse;
import com.qrh.youshangdache.model.vo.rules.FeeRuleResponseVo;
import com.qrh.youshangdache.rules.service.FeeRuleService;
import com.qrh.youshangdache.rules.utils.DroolsUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FeeRuleServiceImpl implements FeeRuleService {

    private static final String FEE_RULE_DRL="rules/FeeRule.drl";

    @Resource
    private KieContainer kieContainer;

    /**
     * 计算订单费用
     * @param feeRuleRequestForm
     * @return
     */
    @Override
    public FeeRuleResponseVo calculateOrderFee(FeeRuleRequestForm feeRuleRequestForm) {
        //封装传入对象
        FeeRuleRequest feeRuleRequest = new FeeRuleRequest();
        feeRuleRequest.setDistance(feeRuleRequestForm.getDistance());
        feeRuleRequest.setStartTime(new DateTime(feeRuleRequestForm.getStartTime()).toString("HH:mm:ss"));
        feeRuleRequest.setWaitMinute(feeRuleRequestForm.getWaitMinute());

        //执行规则，加载规则文件
        KieSession kieSession = DroolsUtils.loadForRule(FEE_RULE_DRL);
        //封装返回对象
        FeeRuleResponse feeRuleResponse = new FeeRuleResponse();
        kieSession.setGlobal("feeRuleResponse", feeRuleResponse);
        // 设置订单对象
        kieSession.insert(feeRuleRequest);
        // 触发规则
        kieSession.fireAllRules();
        // 中止会话
        kieSession.dispose();

        //封装返回对象
        FeeRuleResponseVo feeRuleResponseVo = new FeeRuleResponseVo();
        BeanUtils.copyProperties(feeRuleResponse, feeRuleResponseVo);
        return feeRuleResponseVo;
    }

}
