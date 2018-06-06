package com.dyf.wxapi.annotation.support

import com.dyf.wxapi.annotation.LoginUser
import com.dyf.wxapi.utils.UserTokenManager
import org.springframework.core.MethodParameter
import org.springframework.lang.Nullable
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

class LoginUserHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    static final def LOGIN_TOKEN_KEY = "X-WXMall-Token"

    @Override
    boolean supportsParameter(MethodParameter parameter) {
        parameter.getParameterType().isAssignableFrom(Integer.class) && parameter.hasParameterAnnotation(LoginUser.class)
    }

    @Override
    Object resolveArgument(MethodParameter parameter,
                           @Nullable ModelAndViewContainer container, NativeWebRequest request,
                           @Nullable WebDataBinderFactory factory) throws Exception {
        def token = request.getHeader(LOGIN_TOKEN_KEY)

        if (token == null || token.isEmpty()) {
            return null
        }

        UserTokenManager.getUserId(token)
    }
}
