package com.dyf.apiadmin.web

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.baomidou.mybatisplus.plugins.Page
import com.dyf.apiadmin.annotions.LoginAdmin
import com.dyf.core.utils.HttpResponse
import com.dyf.db.domain.WxMallRegionDO
import com.dyf.db.service.WxMallRegionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/region")
class AdminRegionController {
    @Autowired
    private WxMallRegionService regionService

    @GetMapping("/clist")
    def clist(@LoginAdmin Integer adminId, Integer id) {
        if (id == null) {
            return HttpResponse.badArgument()
        }

        def regionList = regionService.selectList(new EntityWrapper<WxMallRegionDO>()
                .where("pid = {0} and deleted = false"))

        HttpResponse.success(regionList)
    }

    @GetMapping("/list")
    def list(@LoginAdmin Integer adminId,
             String name, Integer code,
             @RequestParam(value = "page", defaultValue = "1") Integer page,
             @RequestParam(value = "limit", defaultValue = "10") Integer limit,
             String sort, String order) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        def wrapper = new EntityWrapper<WxMallRegionDO>()
        if (!StringUtils.isEmpty(name)) {
            wrapper.where("name like {0}", "%" + name + "%")
        }
        if (code != null) {
            wrapper.where("code = {0}", code)
        }
        def regionList = regionService.selectPage(new Page<WxMallRegionDO>(page, limit), wrapper).getRecords()
        int total = regionService.selectCount(wrapper)
        def data = [:]
        data.put("total", total)
        data.put("items", regionList)
        HttpResponse.success(data)
    }
}
