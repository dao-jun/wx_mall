package com.dyf.apiadmin.web

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.dyf.apiadmin.annotions.LoginAdmin
import com.dyf.apiadmin.utils.AdminToken
import com.dyf.apiadmin.utils.AdminTokenManager
import com.dyf.core.utils.HttpResponse
import com.dyf.core.utils.JacksonUtil
import com.dyf.core.utils.bcrypt.BCryptPasswordEncoder
import com.dyf.db.domain.WxMallAdminDO
import com.dyf.db.service.WxMallAdminService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.Assert
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/login")
class AdminAuthController {
    @Autowired
    private WxMallAdminService adminService

    /*
     *  { username : value, password : value }
     */

    @PostMapping("/login")
    def login(@RequestBody String body) {
        String username = JacksonUtil.parseString(body, "username")
        String password = JacksonUtil.parseString(body, "password")

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return HttpResponse.badArgument()
        }

        def adminList = adminService.selectList(new EntityWrapper<WxMallAdminDO>()
                .where("username = {0} and deleted = false", username))
        Assert.state(adminList.size() < 2, "同一个用户名存在两个账户")
        if (adminList.size() == 0) {
            return HttpResponse.badArgumentValue()
        }
        def admin = adminList.get(0)

        def encoder = new BCryptPasswordEncoder()
        if (!encoder.matches(password, admin.getPassword())) {
            return HttpResponse.fail(403, "账号密码不对")
        }

        def adminId = admin.id
        // token
        AdminToken adminToken = AdminTokenManager.generateToken(adminId)

        return HttpResponse.success(adminToken.token)
    }

    /*
     *
     */

    @PostMapping("/logout")
    def login(@LoginAdmin Integer adminId) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }

        return HttpResponse.success()
    }
}
