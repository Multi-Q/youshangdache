package com.qrh.youshangdache.common.login;

import com.qrh.youshangdache.common.constant.RedisConstant;
import com.qrh.youshangdache.common.execption.GuiguException;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.common.util.AuthContextHolder;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author QRH
 * @date 2024/7/17 23:10
 * @description 登录切面类
 */
@Component
@Aspect
public class LoginAspect {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Around(value="execution(* com.atguigu.daijia.*.controller.*.*(..)) && @annotation(guiguLogin)")
    public Object login(ProceedingJoinPoint proceedingJoinPoint,GuiguLogin guiguLogin) throws Throwable{
        //1 获取request对象
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) attributes;
        HttpServletRequest request = sra.getRequest();

        //2 从请求头中获取token
        String token = request.getHeader("token");
        //3 判断token是否为空，如果为空，返回登录提示
        if(!StringUtils.hasText(token)){
            throw new GuiguException(ResultCodeEnum.LOGIN_AUTH);
        }
        //4 token不为空，查询redis
        String customerId = stringRedisTemplate.opsForValue().get(RedisConstant.USER_LOGIN_KEY_PREFIX + token);

        //5 查询redis对应用户ID，把用户id放到threadlocal中
        if(StringUtils.hasText(customerId)){
            AuthContextHolder.setUserId(Long.parseLong(customerId));
        }

        return proceedingJoinPoint.proceed();
    }
}
