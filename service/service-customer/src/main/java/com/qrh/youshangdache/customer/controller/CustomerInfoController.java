package com.qrh.youshangdache.customer.controller;

import com.qrh.youshangdache.common.result.Result;
import com.qrh.youshangdache.customer.service.CustomerInfoService;
import com.qrh.youshangdache.model.form.customer.UpdateWxPhoneForm;
import com.qrh.youshangdache.model.vo.customer.CustomerLoginVo;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/customer/info")
public class CustomerInfoController {

	@Autowired
	private CustomerInfoService customerInfoService;
	/**
	 * 小程序登录接口-用户端
	 * <p>用户第一次登录，则登录并注册，返回用户id；否则直接返回用户ID</p>
	 *
	 * @param code 微信颁发的授权码
	 * @return 用户id
	 */
	@Operation(summary = "用户端微信小程序登录接口")
	@GetMapping("/login/{code}")
	public Result<Long> login(@PathVariable String code){
		return Result.ok(customerInfoService.login(code));
	}

	/**
	 * 获取用户的登录信息
	 *
	 * @param customerId 用户id
	 * @return 用户登录后的基本信息
	 */
	@Operation(summary = "获取客户基本信息")
	@GetMapping("/getCustomerLoginInfo/{customerId}")
	public Result<CustomerLoginVo> getCustomerInfo(@PathVariable Long customerId) {
		return Result.ok(customerInfoService.getCustomerInfo(customerId));
	}
	/**
	 * 绑定用户手机号
	 * <p>登录后检查该用户是否绑定手机号，没有绑定，则提示并要求用户绑定手机号</p>
	 *
	 * @param updateWxPhoneForm
	 * @return true绑定 | false未绑定
	 */
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

