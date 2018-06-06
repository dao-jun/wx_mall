package com.dyf.apiadmin.web

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.baomidou.mybatisplus.plugins.Page
import com.dyf.apiadmin.annotions.LoginAdmin
import com.dyf.core.utils.HttpResponse
import com.dyf.db.domain.WxMallFootPrintDO
import com.dyf.db.service.WxMallFootPrintService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import javax.swing.text.html.parser.Entity

@RestController
@RequestMapping("/admin/footprint")
class AdminFootPrintController {

    @Autowired
    private WxMallFootPrintService footprintService

    @GetMapping("/list")
    def list(@LoginAdmin Integer adminId,
             String userId, String goodsId,
             @RequestParam(value = "page", defaultValue = "1") Integer page,
             @RequestParam(value = "limit", defaultValue = "10") Integer limit,
             String sort, String order) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        def wrapper = new EntityWrapper<WxMallFootPrintDO>()
        if (!StringUtils.isEmpty(userId)) {
            wrapper.where("user_id = {0}", Integer.valueOf(userId))
        }
        if (!StringUtils.isEmpty(goodsId)) {
            wrapper.where("goods_id ={0}", Integer.valueOf(goodsId))
        }
        wrapper.where("deleted = false")
        def footprintList = footprintService.selectPage(new Page<WxMallFootPrintDO>(page, limit), wrapper).getRecords()
        def total = footprintService.selectCount(wrapper)
        def data = [:]
        data.put("total", total)
        data.put("items", footprintList)

        HttpResponse.success(data)
    }

    @PostMapping("/create")
    def create(@LoginAdmin Integer adminId, @RequestBody WxMallFootPrintDO footprint) {
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

        def footprint = footprintService.selectById(id)
        return HttpResponse.success(footprint)
    }

    @PostMapping("/update")
    def update(@LoginAdmin Integer adminId, @RequestBody WxMallFootPrintDO footprint) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        footprintService.updateById(footprint)
        return HttpResponse.success()
    }

    @PostMapping("/delete")
    def delete(@LoginAdmin Integer adminId, @RequestBody WxMallFootPrintDO footprint) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        footprintService.deleteById(footprint.id)
        return HttpResponse.success()
    }
}
