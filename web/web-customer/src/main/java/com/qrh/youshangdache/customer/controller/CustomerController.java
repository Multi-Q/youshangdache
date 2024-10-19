package com.qrh.youshangdache.customer.controller;

import com.atguigu.daijia.common.constant.RedisConstant;
import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.login.Login;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.common.util.AuthContextHolder;
import com.atguigu.daijia.customer.client.CustomerInfoFeignClient;
import com.qrh.youshangdache.customer.service.CustomerService;
import com.atguigu.daijia.model.entity.customer.CustomerInfo;
import com.atguigu.daijia.model.form.customer.UpdateWxPhoneForm;
import com.atguigu.daijia.model.vo.customer.CustomerInfoVo;
import com.atguigu.daijia.model.vo.customer.CustomerLoginVo;
import com.atguigu.daijia.model.vo.order.CurrentOrderInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "客户API接口管理")
@RestController
@RequestMapping("/customer")
@SuppressWarnings({"unchecked", "rawtypes"})
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
    public Result<CurrentOrderInfoVo> searchCustomerCurrentOrder( ) {
        Long customerId = AuthContextHolder.getUserId();
        return Result.ok(customerService.searchCustomerCurrentOrder(customerId));
    }
}

