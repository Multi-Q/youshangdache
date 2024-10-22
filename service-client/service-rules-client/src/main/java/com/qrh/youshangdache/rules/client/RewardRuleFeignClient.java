package com.qrh.youshangdache.rules.client;

import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.model.form.rules.RewardRuleRequestForm;
import com.atguigu.daijia.model.vo.rules.RewardRuleResponseVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "service-rules")
public interface RewardRuleFeignClient {

    @PostMapping("/rules/reward/calculateOrderRewardFee")
    public Result<RewardRuleResponseVo> calculateOrderRewardFee(@RequestBody RewardRuleRequestForm rewardRuleRequestForm);




}