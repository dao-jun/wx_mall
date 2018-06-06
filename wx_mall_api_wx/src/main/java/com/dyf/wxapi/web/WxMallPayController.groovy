package com.dyf.wxapi.web

import com.dyf.core.utils.HttpResponse
import com.dyf.db.domain.WxMallOrderDO
import com.dyf.db.service.WxMallOrderGoodsService
import com.dyf.db.service.WxMallOrderService
import com.dyf.db.service.WxMallUserService
import com.dyf.wxapi.annotation.LoginUser
import com.github.binarywang.wxpay.service.WxPayService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/wx/pay")
class WxMallPayController {

    @Autowired
    private WxMallOrderService orderService
    @Autowired
    private WxMallOrderGoodsService orderGoodsService
    @Autowired
    private WxMallUserService userService

    @Autowired
    private WxPayService wxService

    /**
     * 获取支付的请求参数
     */
    @RequestMapping("/prepay")
    def payPrepay(@LoginUser Integer userId, @RequestBody OrderPayRequest orderId) {
        if (userId == null) {
            return HttpResponse.fail401()
        }
        if (orderId == null) {
            return HttpResponse.fail402()
        }

        def order = orderService.selectById(orderId.orderId)
        def user = userService.selectById(userId)
        if (user.getWeixinOpenid() == null) {
            return HttpResponse.fail(403, "用户openid不存在")
        }
        if (order == null) {
            return HttpResponse.fail(403, "订单不存在")
        }
        def order1 = new WxMallOrderDO()
        order1.id = orderId.orderId
        order1.orderStatus = 201
        orderService.updateById(order1)
        HttpResponse.success("模拟支付成功")
    }

    /**
     * 微信订单回调接口
     */
    @PostMapping(value = "/notify")
    def notify(HttpServletRequest request, HttpServletResponse response) {
        return HttpResponse.fail501()
    }

    /**
     * 订单退款请求
     */
    @RequestMapping("/refund")
    def refund(@LoginUser Integer userId, Integer orderId) {
        if (userId == null) {
            return HttpResponse.fail401()
        }
        if (orderId == null) {
            return HttpResponse.fail402()
        }
        return HttpResponse.fail501()

    }
}

class OrderPayRequest {
    Integer orderId
}
