package com.hnkylin.cloud.manage.interceptor;


import com.hnkylin.cloud.core.config.exception.KylinTokenException;
import com.hnkylin.cloud.manage.config.AuthExcludeApiProperties;
import com.hnkylin.cloud.manage.constant.KylinCloudManageConstants;
import com.hnkylin.cloud.manage.constant.KylinHttpResponseConstants;
import com.hnkylin.cloud.manage.constant.CloudManageRedisConstant;
import com.hnkylin.cloud.manage.service.cache.LoginUserCacheService;
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
public class CloudServiceInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthExcludeApiProperties authExcludeApiProperties;

    @Resource
    private LoginUserCacheService loginUserCacheService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
            Exception {
        String uri = request.getRequestURI();
        String token = request.getHeader(KylinCloudManageConstants.KYLIN_ACCESS_TOKEN);
        log.info("deal request: {}", uri);
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
        //重新设置token过期时间
        loginUserCacheService.vSet(token, redisToken, CloudManageRedisConstant.CLOUD_MANAGE_LOGIN_USER_CACHE_EXPIRE,
                TimeUnit.MILLISECONDS);
        loginUserCacheService.vSet(CloudManageRedisConstant.UID + redisToken, token, CloudManageRedisConstant
                .CLOUD_MANAGE_LOGIN_USER_CACHE_EXPIRE, TimeUnit.MILLISECONDS);
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
