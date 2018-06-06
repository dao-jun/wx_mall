package com.dyf.apiadmin.annotions.support

import com.dyf.apiadmin.annotions.LoginAdmin
import com.dyf.apiadmin.utils.AdminTokenManager
import org.springframework.core.MethodParameter
import org.springframework.lang.Nullable
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

class LoginAdminHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    private static final def LOGIN_TOKEN_KEY = "X-Token"

    @Override
    boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(Integer.class) && parameter.hasParameterAnnotation(LoginAdmin.class)
    }

    @Override
    Object resolveArgument(MethodParameter methodParameter,
                           @Nullable ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest,
                           @Nullable WebDataBinderFactory webDataBinderFactory) throws Exception {
        def token = nativeWebRequest.getHeader(LOGIN_TOKEN_KEY)
        if (token == null || token.isEmpty()) {
            return null
        }

        AdminTokenManager.getUserId(token)
    }
}
