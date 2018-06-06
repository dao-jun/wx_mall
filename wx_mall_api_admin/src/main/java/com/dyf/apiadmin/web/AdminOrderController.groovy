package com.dyf.apiadmin.web

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.baomidou.mybatisplus.plugins.Page
import com.dyf.apiadmin.annotions.LoginAdmin
import com.dyf.core.utils.HttpResponse
import com.dyf.db.domain.WxMallOrderDO
import com.dyf.db.service.WxMallOrderService
import com.dyf.db.utils.OrderUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/order")
class AdminOrderController {

    @Autowired
    private WxMallOrderService orderService

    @GetMapping("/list")
    def list(@LoginAdmin Integer adminId,
             Integer userId, String orderSn,
             @RequestParam(value = "page", defaultValue = "1") Integer page,
             @RequestParam(value = "limit", defaultValue = "10") Integer limit,
             String sort, String order) {
        if (adminId == null) {
            return HttpResponse.fail401()
        }

        def wrapper = new EntityWrapper<WxMallOrderDO>()
        if (userId != null) {
            wrapper.where("user_id = {0}", userId)
        }
        if (!StringUtils.isEmpty(orderSn)) {
            wrapper.where("order_sn = {0}", orderSn)
        }
        wrapper.where("deleted = false")
        def orderList = orderService.selectPage(new Page<WxMallOrderDO>(page, limit), wrapper).getRecords()
        int total = orderService.selectCount(wrapper)

        def data = [:]
        data.put("total", total)
        data.put("items", orderList)

        HttpResponse.success(data)
    }

    /*
     * 目前的逻辑不支持管理员创建
     */

    @PostMapping("/create")
    def create(@LoginAdmin Integer adminId, @RequestBody WxMallOrderDO order) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        HttpResponse.unsupport()
    }

    @GetMapping("/read")
    def read(@LoginAdmin Integer adminId, Integer id) {
        if (adminId == null) {
            return HttpResponse.fail401()
        }

        def order = orderService.selectById(id)
        HttpResponse.success(order)
    }

    /*
     * 目前仅仅支持管理员设置发货相关的信息
     */

    @PostMapping("/update")
    def update(@LoginAdmin Integer adminId, @RequestBody WxMallOrderDO order) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }

        Integer orderId = order.id
        if (orderId == null) {
            return HttpResponse.badArgument()
        }

        def litemallOrder = orderService.selectById(orderId)
        if (litemallOrder == null) {
            return HttpResponse.badArgumentValue()
        }

        if (OrderUtil.isPayStatus(litemallOrder) || OrderUtil.isShipStatus(litemallOrder)) {
            def newOrder = new WxMallOrderDO()
            newOrder.id = orderId
            newOrder.shipChannel = order.shipChannel
            newOrder.shipSn = order.orderSn
            newOrder.shipStartTime = order.shipStartTime
            newOrder.shipEndTime = order.shipEndTime
            newOrder.orderStatus = OrderUtil.STATUS_SHIP
            orderService.updateById(newOrder)
        } else {
            return HttpResponse.badArgumentValue()
        }

        litemallOrder = orderService.selectById(orderId)
        HttpResponse.success(litemallOrder)
    }

    @PostMapping("/delete")
    def delete(@LoginAdmin Integer adminId, @RequestBody WxMallOrderDO order) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        HttpResponse.unsupport()
    }

}
