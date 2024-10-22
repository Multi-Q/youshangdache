package com.qrh.youshangdache.rules.controller;

import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.model.form.payment.ProfitsharingForm;
import com.atguigu.daijia.model.form.rules.ProfitsharingRuleRequestForm;
import com.atguigu.daijia.model.form.rules.RewardRuleRequestForm;
import com.atguigu.daijia.model.vo.rules.ProfitsharingRuleResponseVo;
import com.atguigu.daijia.model.vo.rules.RewardRuleResponseVo;
import com.qrh.youshangdache.rules.service.ProfitsharingRuleService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/rules/profitsharing")
@SuppressWarnings({"unchecked", "rawtypes"})
public class ProfitSharingRuleController {
    @Resource
    private ProfitsharingRuleService  profitsharingRuleService;

    @Operation(summary = "计算订单奖励费用")
    @PostMapping("/calculateProfitsharingFee")
    public Result<ProfitsharingRuleResponseVo> calculateProfitSharingFee(@RequestBody ProfitsharingRuleRequestForm profitsharingRuleRequestForm) {
        return Result.ok(profitsharingRuleService.calculateProfitSharingFee(profitsharingRuleRequestForm));
    }

}

