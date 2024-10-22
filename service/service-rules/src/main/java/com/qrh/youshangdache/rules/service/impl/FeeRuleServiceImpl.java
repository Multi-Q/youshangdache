package com.qrh.youshangdache.rules.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.daijia.model.form.rules.FeeRuleRequest;
import com.atguigu.daijia.model.form.rules.FeeRuleRequestForm;
import com.atguigu.daijia.model.vo.rules.FeeRuleResponse;
import com.atguigu.daijia.model.vo.rules.FeeRuleResponseVo;
import com.qrh.youshangdache.rules.service.FeeRuleService;
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
@SuppressWarnings({"unchecked", "rawtypes"})
public class FeeRuleServiceImpl implements FeeRuleService {
    @Resource
    private KieContainer kieContainer;

    @Override
    public FeeRuleResponseVo calculateOrderFee(FeeRuleRequestForm feeRuleRequestForm) {
        //封装传入对象
        FeeRuleRequest feeRuleRequest = new FeeRuleRequest();
        feeRuleRequest.setDistance(feeRuleRequestForm.getDistance());
        feeRuleRequest.setStartTime(new DateTime(feeRuleRequestForm.getStartTime()).toString("HH:mm:ss"));
        feeRuleRequest.setWaitMinute(feeRuleRequestForm.getWaitMinute());
        // 开启会话
        KieSession kieSession = kieContainer.newKieSession();
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
