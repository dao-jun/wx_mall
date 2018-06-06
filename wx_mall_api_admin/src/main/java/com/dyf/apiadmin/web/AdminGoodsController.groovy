package com.dyf.apiadmin.web

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.baomidou.mybatisplus.plugins.Page
import com.dyf.apiadmin.annotions.LoginAdmin
import com.dyf.core.utils.HttpResponse
import com.dyf.db.domain.WxMallGoodsDO
import com.dyf.db.service.WxMallGoodsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/goods")
class AdminGoodsController {

    @Autowired
    private WxMallGoodsService goodsService

    @GetMapping("/list")
    def list(@LoginAdmin Integer adminId,
             String goodsSn, String name,
             @RequestParam(value = "page", defaultValue = "1") Integer page,
             @RequestParam(value = "limit", defaultValue = "10") Integer limit,
             String sort, String order) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        def wrapper = new EntityWrapper<WxMallGoodsDO>()


        if (!StringUtils.isEmpty(goodsSn)) {
            wrapper.where("goods_sn = {0}", goodsSn)
        }
        if (!StringUtils.isEmpty(name)) {
            wrapper.where("name like {0}", "%" + name + "%")
        }
        wrapper.where("deleted = false")
        def goodsList = goodsService.selectPage(new Page<WxMallGoodsDO>(page, limit), wrapper).getRecords()
        def total = goodsService.selectCount(wrapper)
        def data = [:]
        data.put("total", total)
        data.put("items", goodsList)

        return HttpResponse.success(data)
    }

    @PostMapping("/create")
    def create(@LoginAdmin Integer adminId, @RequestBody WxMallGoodsDO goods) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        goodsService.insert(goods)
        return HttpResponse.success(goods)
    }

    @GetMapping("/read")
    def read(@LoginAdmin Integer adminId, Integer id) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }

        if (id == null) {
            return HttpResponse.badArgument()
        }

        def goods = goodsService.selectById(id)
        return HttpResponse.success(goods)
    }

    @PostMapping("/update")
    def update(@LoginAdmin Integer adminId, @RequestBody WxMallGoodsDO goods) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        goodsService.updateById(goods)
        return HttpResponse.success(goods)
    }

    @PostMapping("/delete")
    def delete(@LoginAdmin Integer adminId, @RequestBody WxMallGoodsDO goods) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        goodsService.deleteById(goods.id)
        return HttpResponse.success()
    }

}
