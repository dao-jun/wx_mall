package com.dyf.apiadmin.web

import com.alibaba.druid.util.StringUtils
import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.baomidou.mybatisplus.plugins.Page
import com.dyf.apiadmin.annotions.LoginAdmin
import com.dyf.core.utils.HttpResponse
import com.dyf.db.domain.WxMallAdDO
import com.dyf.db.service.WxMallAdService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/ad")
class AdminAdController {
    @Autowired
    private WxMallAdService adService

    @GetMapping("/list")
    def list(@LoginAdmin Integer adminId,
             String name, String content,
             @RequestParam(value = "page", defaultValue = "1") Integer page,
             @RequestParam(value = "limit", defaultValue = "10") Integer limit,
             String sort, String order) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }

        def wrapper = new EntityWrapper<WxMallAdDO>()
        if (!StringUtils.isEmpty(name)) {
            wrapper.where("name like {0}", "%" + name + "%")
        }
        if (!org.springframework.util.StringUtils.isEmpty(content)) {
            wrapper.where("content like {0}", "%" + content + "%")
        }
        wrapper.where("deleted = false")
        def adList = adService.selectPage(new Page<WxMallAdDO>(page, limit), wrapper).getRecords()
        int total = adService.selectCount(wrapper)
        def data = [:]
        data.put("total", total)
        data.put("items", adList)

        return HttpResponse.success(data)
    }

    @PostMapping("/create")
    def create(@LoginAdmin Integer adminId, @RequestBody WxMallAdDO ad) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        adService.insert(ad)
        return HttpResponse.success(ad)
    }

    @GetMapping("/read")
    def read(@LoginAdmin Integer adminId, Integer id) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }

        if (id == null) {
            return HttpResponse.badArgument()
        }

        WxMallAdDO brand = adService.selectById(id)
        return HttpResponse.success(brand)
    }

    @PostMapping("/update")
    def update(@LoginAdmin Integer adminId, @RequestBody WxMallAdDO ad) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        adService.updateById(ad)
        return HttpResponse.success(ad)
    }

    @PostMapping("/delete")
    def delete(@LoginAdmin Integer adminId, @RequestBody WxMallAdDO ad) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        adService.deleteById(ad.id)
        return HttpResponse.success()
    }

}
