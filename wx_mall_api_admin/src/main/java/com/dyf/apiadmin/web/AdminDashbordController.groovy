package com.dyf.apiadmin.web

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.dyf.apiadmin.annotions.LoginAdmin
import com.dyf.core.utils.HttpResponse
import com.dyf.db.service.WxMallGoodsService
import com.dyf.db.service.WxMallOrderService
import com.dyf.db.service.WxMallProductService
import com.dyf.db.service.WxMallUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/dashboard")
class AdminDashbordController {
    @Autowired
    private WxMallUserService userService
    @Autowired
    private WxMallGoodsService goodsService
    @Autowired
    private WxMallProductService productService
    @Autowired
    private WxMallOrderService orderService

    @GetMapping("")
    def info(@LoginAdmin Integer adminId) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        def wapper = new EntityWrapper()
        wapper.where("deleted = false")
        def userTotal = userService.selectCount(wapper)
        def goodsTotal = goodsService.selectCount(wapper)
        def productTotal = productService.selectCount(wapper)
        def orderTotal = orderService.selectCount(wapper)
        def data = new HashMap()
        data.put("userTotal", userTotal)
        data.put("goodsTotal", goodsTotal)
        data.put("productTotal", productTotal)
        data.put("orderTotal", orderTotal)

        return HttpResponse.success(data)
    }
}
