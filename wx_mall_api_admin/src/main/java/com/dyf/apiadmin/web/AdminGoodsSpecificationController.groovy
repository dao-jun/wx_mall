package com.dyf.apiadmin.web

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.baomidou.mybatisplus.plugins.Page
import com.dyf.apiadmin.annotions.LoginAdmin
import com.dyf.core.utils.HttpResponse
import com.dyf.db.domain.WxMallGoodsSpecificationDO
import com.dyf.db.service.WxMallGoodsSpecificationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/goods-specification")
class AdminGoodsSpecificationController {
    @Autowired
    private WxMallGoodsSpecificationService goodsSpecificationService

    @GetMapping("/list")
    def list(@LoginAdmin Integer adminId,
             Integer goodsId,
             @RequestParam(value = "page", defaultValue = "1") Integer page,
             @RequestParam(value = "limit", defaultValue = "10") Integer limit,
             String sort, String order) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        def wrapper = new EntityWrapper<WxMallGoodsSpecificationDO>()
        if (goodsId != null) {
            wrapper.where("goods_id = {0}", goodsId)
        }
        wrapper.where("deleted  = false")
        def goodsSpecificationList = goodsSpecificationService.selectPage(new Page<WxMallGoodsSpecificationDO>(page, limit), wrapper).getRecords()
        int total = goodsSpecificationService.selectCount(wrapper)
        def data = [:]
        data.put("total", total)
        data.put("items", goodsSpecificationList)

        HttpResponse.success(data)
    }

    @PostMapping("/create")
    def create(@LoginAdmin Integer adminId, @RequestBody WxMallGoodsSpecificationDO goodsSpecification) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        goodsSpecificationService.insert(goodsSpecification)
        HttpResponse.success(goodsSpecification)
    }

    @GetMapping("/read")
    def read(@LoginAdmin Integer adminId, Integer id) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }

        if (id == null) {
            return HttpResponse.badArgument()
        }

        def goodsSpecification = goodsSpecificationService.selectById(id)
        HttpResponse.success(goodsSpecification)
    }

    @PostMapping("/update")
    def update(@LoginAdmin Integer adminId, @RequestBody WxMallGoodsSpecificationDO goodsSpecification) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        goodsSpecificationService.updateById(goodsSpecification)
        HttpResponse.success(goodsSpecification)
    }

    @PostMapping("/delete")
    def delete(@LoginAdmin Integer adminId, @RequestBody WxMallGoodsSpecificationDO goodsSpecification) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        goodsSpecificationService.deleteById(goodsSpecification.id)
        return HttpResponse.success()
    }

    @GetMapping("/volist")
    def volist(@LoginAdmin Integer adminId, Integer id) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }

        if (id == null) {
            return HttpResponse.badArgument()
        }

        def goodsSpecificationVoList = goodsSpecificationService.getSpecificationVoList(id)
        HttpResponse.success(goodsSpecificationVoList)
    }

}
