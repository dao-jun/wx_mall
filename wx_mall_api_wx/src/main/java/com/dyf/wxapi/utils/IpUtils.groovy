package com.dyf.wxapi.utils

import javax.servlet.http.HttpServletRequest

class IpUtils {

    static def client(HttpServletRequest request) {
        def xff = request.getHeader("x-forwarded-for")
        if (xff == null) {
            xff = request.getRemoteAddr()
        }

        xff
    }
}
