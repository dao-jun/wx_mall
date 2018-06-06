package com.dyf.apiadmin.web

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.baomidou.mybatisplus.plugins.Page
import com.dyf.apiadmin.annotions.LoginAdmin
import com.dyf.core.utils.HttpResponse
import com.dyf.db.domain.WxMallCartDO
import com.dyf.db.service.WxMallCartService
import com.dyf.db.service.WxMallGoodsService
import com.dyf.db.service.WxMallProductService
import com.dyf.db.service.WxMallUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/cart")
class AdminCartController {

    @Autowired
    private WxMallCartService cartService
    @Autowired
    private WxMallUserService userService
    @Autowired
    private WxMallGoodsService goodsService
    @Autowired
    private WxMallProductService productService

    @GetMapping("/list")
    def list(@LoginAdmin Integer adminId,
             Integer userId, Integer goodsId,
             @RequestParam(value = "page", defaultValue = "1") Integer page,
             @RequestParam(value = "limit", defaultValue = "10") Integer limit,
             String sort, String order) {
        if (adminId == null) {
            return HttpResponse.fail401()
        }
        def wrapper = new EntityWrapper<WxMallCartDO>()
        if (userId != null) {
            wrapper.where("user_id = {0}", userId)
        }
        if (goodsId != null) {
            wrapper.where("goods_id = {0}", goodsId)
        }
        wrapper.where("deleted = false")
        def cartList = cartService.selectPage(new Page<WxMallCartDO>(page, limit), wrapper).getRecords()
        def total = cartService.selectCount(wrapper)

        def data = [:]
        data.put("total", total)
        data.put("items", cartList)

        HttpResponse.success(data)
    }

    /*
     * 目前的逻辑不支持管理员创建
     */

    @PostMapping("/create")
    def create(@LoginAdmin Integer adminId, @RequestBody WxMallCartDO cart) {
        if (adminId == null) {
            return HttpResponse.fail401()
        }

        HttpResponse.fail501()
    }

    @GetMapping("/read")
    def read(@LoginAdmin Integer adminId, Integer id) {
        if (adminId == null) {
            return HttpResponse.fail401()
        }

        def cart = cartService.selectById(id)
        HttpResponse.success(cart)
    }

    /*
     * 目前的逻辑不支持管理员创建
     */

    @PostMapping("/update")
    def update(@LoginAdmin Integer adminId, @RequestBody WxMallCartDO cart) {
        if (adminId == null) {
            return HttpResponse.fail401()
        }
        HttpResponse.fail501()
    }

    @PostMapping("/delete")
    def delete(@LoginAdmin Integer adminId, @RequestBody WxMallCartDO cart) {
        if (adminId == null) {
            return HttpResponse.fail401()
        }
        cartService.deleteById(cart.id)
        HttpResponse.success()
    }


}
