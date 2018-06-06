package com.dyf.apiadmin.web

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.baomidou.mybatisplus.plugins.Page
import com.dyf.apiadmin.annotions.LoginAdmin
import com.dyf.core.utils.HttpResponse
import com.dyf.db.domain.WxMallCollectDO
import com.dyf.db.service.WxMallCollectService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/collect")
class AdminCollectController {
    @Autowired
    private WxMallCollectService collectService

    @GetMapping("/list")
    def list(@LoginAdmin Integer adminId,
             String userId, String valueId,
             @RequestParam(value = "page", defaultValue = "1") Integer page,
             @RequestParam(value = "limit", defaultValue = "10") Integer limit,
             String sort, String order) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        def wrapper = new EntityWrapper<WxMallCollectDO>()
        if (!StringUtils.isEmpty(userId)) {
            wrapper.where("user_id = {0}", Integer.valueOf(userId))
        }
        if (!StringUtils.isEmpty(valueId)) {
            wrapper.where("value_ud = {0}", Integer.valueOf(valueId))
        }
        wrapper.where("deleted = false")
        def collectList = collectService.selectPage(new Page<WxMallCollectDO>(page, limit), wrapper).getRecords()
        def total = collectService.selectCount(wrapper)
        def data = [:]
        data.put("total", total)
        data.put("items", collectList)

        HttpResponse.success(data)
    }

    @PostMapping("/create")
    def create(@LoginAdmin Integer adminId, @RequestBody WxMallCollectDO collect) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        HttpResponse.unsupport()
    }

    @GetMapping("/read")
    def read(@LoginAdmin Integer adminId, Integer id) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }

        if (id == null) {
            return HttpResponse.badArgument()
        }

        def collect = collectService.selectById(id)
        return HttpResponse.success(collect)
    }

    @PostMapping("/update")
    def update(@LoginAdmin Integer adminId, @RequestBody WxMallCollectDO collect) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        collectService.updateById(collect)
        return HttpResponse.success()
    }

    @PostMapping("/delete")
    def delete(@LoginAdmin Integer adminId, @RequestBody WxMallCollectDO collect) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        collectService.deleteById(collect.id)
        return HttpResponse.success()
    }
}
