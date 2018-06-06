package com.dyf.apiadmin.web

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.baomidou.mybatisplus.plugins.Page
import com.dyf.apiadmin.annotions.LoginAdmin
import com.dyf.core.utils.HttpResponse
import com.dyf.db.domain.WxMallUserDO
import com.dyf.db.service.WxMallUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/user")
class AdminUserController {
    @Autowired
    private WxMallUserService userService

    @GetMapping("/list")
    def list(@LoginAdmin Integer adminId,
             String username, String mobile,
             @RequestParam(value = "page", defaultValue = "1") Integer page,
             @RequestParam(value = "limit", defaultValue = "10") Integer limit,
             String sort, String order) {
        if (adminId == null) {
            return HttpResponse.fail401()
        }

        def wrapper = new EntityWrapper<WxMallUserDO>()
        if (!StringUtils.isEmpty(username)) {
            wrapper.where("username like {0}", "%" + username + "%")
        }
        if (!StringUtils.isEmpty(mobile)) {
            wrapper.where("mobile = {0}", mobile)
        }
        wrapper.where("deleted = false")
        def userList = userService.selectPage(new Page<WxMallUserDO>(page, limit), wrapper).getRecords()
        int total = userService.selectCount(wrapper)
        def data = [:]
        data.put("total", total)
        data.put("items", userList)

        HttpResponse.success(data)
    }

    @GetMapping("/username")
    def username(String username) {
        if (StringUtils.isEmpty(username)) {
            return HttpResponse.fail402()
        }

        int total = userService.selectCount(new EntityWrapper<WxMallUserDO>()
                .where("username = {0} and deleted =false", username))
        if (total == 0) {
            return HttpResponse.success("不存在")
        }
        HttpResponse.success("已存在")
    }


    @PostMapping("/create")
    def create(@LoginAdmin Integer adminId, @RequestBody WxMallUserDO user) {


        userService.insert(user)
        return HttpResponse.success(user)
    }

    @PostMapping("/update")
    def update(@LoginAdmin Integer adminId, @RequestBody WxMallUserDO user) {


        userService.updateById(user)
        return HttpResponse.success(user)
    }
}
