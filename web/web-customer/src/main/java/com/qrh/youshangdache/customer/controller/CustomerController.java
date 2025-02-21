package com.qrh.youshangdache.customer.controller;

import com.qrh.youshangdache.common.login.Login;
import com.qrh.youshangdache.common.result.Result;
import com.qrh.youshangdache.common.util.AuthContextHolder;
import com.qrh.youshangdache.customer.service.CustomerService;
import com.qrh.youshangdache.model.form.customer.UpdateWxPhoneForm;
import com.qrh.youshangdache.model.vo.customer.CustomerLoginVo;
import com.qrh.youshangdache.model.vo.order.CurrentOrderInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "客户API接口管理")
@RestController
@RequestMapping("/customer")
public class CustomerController {
    @Resource
    private CustomerService customerService;

    @Operation(summary = "小程序授权登录")
    @GetMapping(value = "/login/{code}")
    public Result<String> wxLogin(@PathVariable String code) {
        return Result.ok(customerService.login(code));
    }

    @Operation(summary = "获取登录后的用户信息")
    @GetMapping("/getCustomerLoginInfo")
    @Login
    public Result<CustomerLoginVo> getCustomerInfo() {
        return Result.ok(customerService.getCustomerLoginInfo(AuthContextHolder.getUserId()));
    }

    @Operation(summary = "登录后检查该用户是否绑定手机号，没有绑定，则提示并要求用户绑定手机号")
    @GetMapping("/updateWxPhone")
    @Login
    public Result<Boolean> updateWxPhone(@RequestBody UpdateWxPhoneForm updateWxPhoneForm) {
        //用于微信公众号个人版不能获取用户的手机号，所以这里直接硬编码写死返回true
        //        Boolean bool = customerService.updateWxPhoneNumber(updateWxPhoneForm);
        return Result.ok(true);
    }

    @Operation(summary = "乘客端乘客查找当前订单")
    @GetMapping("/searchCustomerCurrentOrder")
    @Login
    public Result<CurrentOrderInfoVo> searchCustomerCurrentOrder() {
        Long customerId = AuthContextHolder.getUserId();
        return Result.ok(customerService.searchCustomerCurrentOrder(customerId));
    }
}

