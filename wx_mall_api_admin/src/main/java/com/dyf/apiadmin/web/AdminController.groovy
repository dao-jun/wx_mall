package com.dyf.apiadmin.web

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.baomidou.mybatisplus.plugins.Page
import com.dyf.apiadmin.annotions.LoginAdmin
import com.dyf.apiadmin.utils.AdminTokenManager
import com.dyf.core.utils.HttpResponse
import com.dyf.core.utils.bcrypt.BCryptPasswordEncoder
import com.dyf.db.domain.WxMallAdminDO
import com.dyf.db.service.WxMallAdminService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/admin")
class AdminController {
    @Autowired
    private WxMallAdminService adminService


    @GetMapping("/info")
    def info(String token) {
        Integer adminId = AdminTokenManager.getUserId(token)
        if (adminId == null) {
            return HttpResponse.badArgumentValue()
        }
        def admin = adminService.selectById(adminId)
        if (admin == null) {
            return HttpResponse.badArgumentValue()
        }

        def data = [:]
        data.put("name", admin.username)
        data.put("avatar", admin.avatar)

        // 目前roles不支持，这里简单设置admin
        def roles = new ArrayList<>()
        roles.add("admin")
        data.put("roles", roles)
        data.put("introduction", "admin introduction")
        HttpResponse.success(data)
    }

    @GetMapping("/list")
    def list(@LoginAdmin Integer adminId,
             String username,
             @RequestParam(value = "page", defaultValue = "1") Integer page,
             @RequestParam(value = "limit", defaultValue = "10") Integer limit,
             String sort, String order) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        def wrapper = new EntityWrapper<WxMallAdminDO>()

        if (!StringUtils.isEmpty(username)) {
            wrapper.where("username like {0}", "%" + username + "%")
        }
        wrapper.where("deleted = false")
        def adminList = adminService.selectPage(new Page<WxMallAdminDO>(page, limit), wrapper).getRecords()

        def total = adminService.selectCount(wrapper)
        def data = [:]
        data.put("total", total)
        data.put("items", adminList)

        HttpResponse.success(data)
    }

    @PostMapping("/create")
    def create(@LoginAdmin Integer adminId, @RequestBody WxMallAdminDO admin) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }

        def rawPassword = admin.password
        def encoder = new BCryptPasswordEncoder()
        def encodedPassword = encoder.encode(rawPassword)
        admin.password = encodedPassword

        adminService.insert(admin)
        HttpResponse.success(admin)
    }

    @GetMapping("/read")
    def read(@LoginAdmin Integer adminId, Integer id) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }

        if (id == null) {
            return HttpResponse.badArgument()
        }

        def admin = adminService.selectById(id)
        HttpResponse.success(admin)
    }

    @PostMapping("/update")
    def update(@LoginAdmin Integer adminId, @RequestBody WxMallAdminDO admin) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }

        Integer anotherAdminId = admin.id
        if (anotherAdminId.intValue() == 1) {
            return HttpResponse.fail(403, "超级管理员不能修改")
        }

        adminService.updateById(admin)
        HttpResponse.success(admin)
    }

    @PostMapping("/delete")
    def delete(@LoginAdmin Integer adminId, @RequestBody WxMallAdminDO admin) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }

        def anotherAdminId = admin.id
        if (anotherAdminId.intValue() == 1) {
            return HttpResponse.fail(403, "超级管理员不能删除")
        }
        adminService.deleteById(anotherAdminId)
        HttpResponse.success()
    }
}
