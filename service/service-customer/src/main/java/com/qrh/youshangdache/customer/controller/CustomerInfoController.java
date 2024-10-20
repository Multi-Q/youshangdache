package com.qrh.youshangdache.customer.controller;

import com.atguigu.daijia.common.result.Result;
import com.qrh.youshangdache.customer.service.CustomerInfoService;
import com.atguigu.daijia.model.entity.customer.CustomerInfo;
import com.atguigu.daijia.model.form.customer.UpdateWxPhoneForm;
import com.atguigu.daijia.model.vo.customer.CustomerLoginVo;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/customer/info")
@SuppressWarnings({"unchecked", "rawtypes"})
public class CustomerInfoController {

	@Autowired
	private CustomerInfoService customerInfoService;

	@Operation(summary = "登录接口")
	@GetMapping("/login/{code}")
	public Result<Long> login(@PathVariable String code){
		return Result.ok(customerInfoService.login(code));
	}


	@Operation(summary = "获取客户基本信息")
	@GetMapping("/getCustomerLoginInfo/{customerId}")
	public Result<CustomerLoginVo> getCustomerInfo(@PathVariable Long customerId) {
		return Result.ok(customerInfoService.getCustomerInfo(customerId));
	}

	@Operation(summary = "更新客户微信手机号码")
	@GetMapping("/updateWxPhoneNumber")
	public Result<Boolean> updateWxPhoneNumber(@RequestBody UpdateWxPhoneForm updateWxPhoneForm) {
		return Result.ok(customerInfoService.updateWxPhoneNumber(updateWxPhoneForm));
	}
	@Operation(summary = "获取客户的openId")
	@GetMapping("/getCustomerOpenId/{customerId}")
	public Result<String> getCustomerOpenId(@PathVariable Long customerId) {
		return Result.ok(customerInfoService.getCustomerOpenId(customerId));
	}

}

