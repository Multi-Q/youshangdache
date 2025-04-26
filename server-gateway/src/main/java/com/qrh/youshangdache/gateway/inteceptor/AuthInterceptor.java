package com.qrh.youshangdache.gateway.inteceptor;

import com.qrh.youshangdache.common.constant.RedisConstant;
import com.qrh.youshangdache.common.result.ResultCodeEnum;
import com.qrh.youshangdache.common.util.AuthContextHolder;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 处理登录的拦截器，拦截所有请求
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if (!StringUtils.hasText(token)) {
            response.setStatus(ResultCodeEnum.LOGIN_AUTH.getCode());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    """
                       {"code": %d, "message": "%s"}
                    """.formatted(ResultCodeEnum.LOGIN_AUTH.getCode(), ResultCodeEnum.LOGIN_AUTH.getMessage())
            );
            return false;
        }
        //4 token不为空，查询redis
        String customerId = stringRedisTemplate.opsForValue().get(RedisConstant.USER_LOGIN_KEY_PREFIX + token);

        //5 查询redis对应用户ID，把用户id放到threadlocal中
        if (StringUtils.hasText(customerId)) {
            AuthContextHolder.setUserId(Long.parseLong(customerId));
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        AuthContextHolder.removeUserId();
    }
}
