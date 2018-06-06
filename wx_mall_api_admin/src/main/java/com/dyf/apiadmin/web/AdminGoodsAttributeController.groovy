package com.dyf.apiadmin.web

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.baomidou.mybatisplus.plugins.Page
import com.dyf.apiadmin.annotions.LoginAdmin
import com.dyf.core.utils.HttpResponse
import com.dyf.db.domain.WxMallGoodsAttributeDO
import com.dyf.db.service.WxMallGoodsAttributeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/goods-attribute")
class AdminGoodsAttributeController {
    @Autowired
    private WxMallGoodsAttributeService goodsAttributeService

    @GetMapping("/list")
    def list(@LoginAdmin Integer adminId,
             Integer goodsId,
             @RequestParam(value = "page", defaultValue = "1") Integer page,
             @RequestParam(value = "limit", defaultValue = "10") Integer limit,
             String sort, String order) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        def wrapper = new EntityWrapper<WxMallGoodsAttributeDO>()
        if (goodsId != null) {
            wrapper.where("goods_id = {0}", goodsId)
        }
        wrapper.where("deleted = false")
        def goodsAttributeList = goodsAttributeService.selectPage(new Page<WxMallGoodsAttributeDO>(page, limit), wrapper).getRecords()
        int total = goodsAttributeService.selectCount(wrapper)
        def data = new HashMap()
        data.put("total", total)
        data.put("items", goodsAttributeList)

        return HttpResponse.success(data)
    }

    @PostMapping("/create")
    def create(@LoginAdmin Integer adminId, @RequestBody WxMallGoodsAttributeDO goodsAttribute) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        goodsAttributeService.insert(goodsAttribute)
        return HttpResponse.success(goodsAttribute)
    }

    @GetMapping("/read")
    def read(@LoginAdmin Integer adminId, Integer id) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }

        if (id == null) {
            return HttpResponse.badArgument()
        }

        def goodsAttribute = goodsAttributeService.selectById(id)
        HttpResponse.success(goodsAttribute)
    }

    @PostMapping("/update")
    def update(@LoginAdmin Integer adminId, @RequestBody WxMallGoodsAttributeDO goodsAttribute) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        goodsAttributeService.updateById(goodsAttribute)
        HttpResponse.success(goodsAttribute)
    }

    @PostMapping("/delete")
    def delete(@LoginAdmin Integer adminId, @RequestBody WxMallGoodsAttributeDO goodsAttribute) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        goodsAttributeService.deleteById(goodsAttribute.id)
        HttpResponse.success()
    }

}
