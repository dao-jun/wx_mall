package com.dyf.core.config

import com.dyf.core.utils.HttpResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    def argumentHandler(MethodArgumentTypeMismatchException e) {
        e.printStackTrace()
        HttpResponse.badArgumentValue()
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    def exceptionHandler(Exception e) {
        e.printStackTrace()
        HttpResponse.serious()
    }
}
