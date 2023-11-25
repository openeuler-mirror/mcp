package com.hnkylin.cloud.selfservice.interceptor;


import com.hnkylin.cloud.core.config.exception.KylinTokenException;
import com.hnkylin.cloud.selfservice.config.AuthExcludeApiProperties;
import com.hnkylin.cloud.selfservice.constant.KylinHttpResponseConstants;
import com.hnkylin.cloud.selfservice.constant.KylinSelfConstants;
import com.hnkylin.cloud.selfservice.constant.SelfServiceRedisConstant;
import com.hnkylin.cloud.selfservice.service.cache.LoginUserCacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


@Slf4j
public class SelfServiceInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthExcludeApiProperties authExcludeApiProperties;

    @Resource
    private LoginUserCacheService loginUserCacheService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
            Exception {
        String uri = request.getRequestURI();
        String token = request.getHeader(KylinSelfConstants.KYLIN_ACCESS_TOKEN);
        String TIME_REFRESH = request.getHeader(KylinSelfConstants.TIME_REFRESH);
        log.info("requestUrl: {}", uri);
        List<String> exApis = authExcludeApiProperties.getAuthExcludeApis();

        if (exApis.contains(uri)) {
            return true;
        }


        //判断token是否存在
        if (StringUtils.isBlank(token)) {
            throw new KylinTokenException(KylinHttpResponseConstants.NOT_EXIST_TOKEN);
        }
        Object redisToken = loginUserCacheService.vGet(token);
        if (Objects.isNull(redisToken)) {
            //token过期，重新登录
            throw new KylinTokenException(KylinHttpResponseConstants.TOKEN_EXPIRE);
        }
        if (StringUtils.isBlank(TIME_REFRESH)) {
            //重新设置token过期时间
            loginUserCacheService.vSet(token, redisToken, SelfServiceRedisConstant.SELF_SERVICE_LOGIN_USER_CACHE_EXPIRE,
                    TimeUnit.MILLISECONDS);
            loginUserCacheService.vSet(SelfServiceRedisConstant.UID + redisToken, token, SelfServiceRedisConstant
                    .SELF_SERVICE_LOGIN_USER_CACHE_EXPIRE, TimeUnit.MILLISECONDS);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable
            ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable
            Exception ex) throws Exception {
    }

}
