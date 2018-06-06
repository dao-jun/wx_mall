package com.dyf.core.utils

class HttpResponse {

    static Object success() {
        Map<String, Object> obj = new HashMap<String, Object>()
        obj.put("errno", 0)
        obj.put("errmsg", "成功")
        obj
    }

    static Object success(Object data) {
        Map<String, Object> obj = new HashMap<String, Object>()
        obj.put("errno", 0)
        obj.put("errmsg", "成功")
        obj.put("data", data)
        obj
    }

    static Object success(String errmsg, Object data) {
        Map<String, Object> obj = new HashMap<String, Object>()
        obj.put("errno", 0)
        obj.put("errmsg", errmsg)
        obj.put("data", data)
        obj
    }

    static Object fail() {
        Map<String, Object> obj = new HashMap<String, Object>()
        obj.put("errno", -1)
        obj.put("errmsg", "错误")
        obj
    }

    static Object fail(int errno, String errmsg) {
        Map<String, Object> obj = new HashMap<String, Object>()
        obj.put("errno", errno)
        obj.put("errmsg", errmsg)
        obj
    }

    static Object fail401() {
        fail(401, "请登录")
    }

    static Object unlogin() {
        fail401()
    }

    static Object fail402() {
        fail(402, "参数不对")
    }

    static Object badArgument() {
        fail402()
    }

    static Object fail403() {
        fail(403, "参数值不对")
    }

    static Object badArgumentValue() {
        return fail403()
    }

    static Object fail501() {
        fail(501, "业务不支持")
    }

    static Object unsupport() {
        fail501()
    }

    static Object fail502() {
        fail(502, "系统内部错误")
    }

    static Object serious() {
        fail502()
    }
}
