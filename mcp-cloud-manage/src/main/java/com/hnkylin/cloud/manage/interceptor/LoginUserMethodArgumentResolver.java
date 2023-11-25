package com.hnkylin.cloud.manage.interceptor;


import com.hnkylin.cloud.core.annotation.LoginUser;
import com.hnkylin.cloud.core.common.JwtUtil;
import com.hnkylin.cloud.core.config.exception.KylinTokenException;
import com.hnkylin.cloud.manage.constant.KylinHttpResponseConstants;
import com.hnkylin.cloud.manage.constant.KylinCloudManageConstants;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Objects;


public class LoginUserMethodArgumentResolver implements HandlerMethodArgumentResolver {


    public LoginUserMethodArgumentResolver() {

    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        return parameter.hasParameterAnnotation(LoginUser.class) && parameter.getParameterType().equals(LoginUserVo
                .class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {

        String token = webRequest.getHeader(KylinCloudManageConstants.KYLIN_ACCESS_TOKEN);
        if (Objects.isNull(token)) {
            throw new KylinTokenException(KylinHttpResponseConstants.NOT_EXIST_TOKEN);
        }
        String username = JwtUtil.getUsername(token);
        String userId = JwtUtil.getUserId(token);

        LoginUserVo loginUserVo = new LoginUserVo();
        loginUserVo.setUserId(Integer.parseInt(userId));
        loginUserVo.setUserName(username);
        loginUserVo.setToken(token);
        return loginUserVo;
    }

}
