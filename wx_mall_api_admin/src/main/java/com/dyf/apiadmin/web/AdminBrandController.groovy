package com.dyf.apiadmin.web

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.baomidou.mybatisplus.plugins.Page
import com.dyf.apiadmin.annotions.LoginAdmin
import com.dyf.core.utils.HttpResponse
import com.dyf.db.domain.WxMallBrandDO
import com.dyf.db.service.WxMallBrandService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/brand")
class AdminBrandController {

    @Autowired
    private WxMallBrandService brandService

    @GetMapping("/list")
    def list(@LoginAdmin Integer adminId,
             String id, String name,
             @RequestParam(value = "page", defaultValue = "1") Integer page,
             @RequestParam(value = "limit", defaultValue = "10") Integer limit,
             String sort, String order) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        def wrapper = new EntityWrapper<WxMallBrandDO>()
        if (!StringUtils.isEmpty(id)) {
            wrapper.where("id = {0}", Integer.valueOf(id))
        }
        if (!StringUtils.isEmpty(name)) {
            wrapper.where("name like {0}", "%" + name + "%")
        }
        wrapper.where("deleted = false")
        def brandList = brandService.selectPage(new Page<WxMallBrandDO>(page, limit), wrapper).getRecords()
        def total = brandService.selectCount(wrapper)
        def data = new HashMap()
        data.put("total", total)
        data.put("items", brandList)

        HttpResponse.success(data)
    }

    @PostMapping("/create")
    def create(@LoginAdmin Integer adminId, @RequestBody WxMallBrandDO brand) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        brandService.insert(brand)
        HttpResponse.success(brand)
    }

    @GetMapping("/read")
    def read(@LoginAdmin Integer adminId, Integer id) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }

        if (id == null) {
            return HttpResponse.badArgument()
        }

        def brand = brandService.selectById(id)
        HttpResponse.success(brand)
    }

    @PostMapping("/update")
    def update(@LoginAdmin Integer adminId, @RequestBody WxMallBrandDO brand) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        brandService.updateById(brand)
        HttpResponse.success(brand)
    }

    @PostMapping("/delete")
    def delete(@LoginAdmin Integer adminId, @RequestBody WxMallBrandDO brand) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        brandService.deleteById(brand.id)
        return HttpResponse.success()
    }
}