package com.dyf.wxapi.web

import com.alibaba.druid.support.json.JSONUtils
import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.dyf.core.utils.HttpResponse
import com.dyf.core.utils.JacksonUtil
import com.dyf.db.domain.WxMallAddressDO
import com.dyf.db.domain.WxMallCartDO
import com.dyf.db.domain.WxMallOrderDO
import com.dyf.db.domain.WxMallOrderGoodsDO
import com.dyf.db.domain.WxMallProductDO
import com.dyf.db.service.WxMallAddressService
import com.dyf.db.service.WxMallCartService
import com.dyf.db.service.WxMallOrderGoodsService
import com.dyf.db.service.WxMallOrderService
import com.dyf.db.service.WxMallProductService
import com.dyf.db.service.WxMallRegionAreaService
import com.dyf.db.service.WxMallRegionCityService
import com.dyf.db.service.WxMallRegionProvinceService
import com.dyf.db.service.WxMallRegionStreetService
import com.dyf.db.utils.OrderUtil
import com.dyf.wxapi.annotation.LoginUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.DefaultTransactionDefinition
import org.springframework.util.Assert
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import java.time.LocalDate
import java.time.LocalDateTime

@RestController
@RequestMapping("/wx/order")
class WxMallOrderController {

    @Autowired
    private PlatformTransactionManager txManager
    @Autowired
    private WxMallOrderService orderService
    @Autowired
    private WxMallAddressService addressService
    @Autowired
    private WxMallCartService cartService
    @Autowired
    private WxMallRegionStreetService streetService
    @Autowired
    private WxMallRegionCityService cityService
    @Autowired
    private WxMallRegionProvinceService provinceService
    @Autowired
    private WxMallRegionAreaService areaService
    @Autowired
    private WxMallProductService productService
    @Autowired
    private WxMallOrderGoodsService orderGoodsService

    private def detailedAddress(WxMallAddressDO addressDO) {
        def provinceId = addressDO.provinceId
        def cityId = addressDO.cityId
        def areaId = addressDO.areaId
        def provinceName = provinceService.selectById(provinceId).name
        def cityName = cityService.selectById(cityId).name
        def areaName = areaService.selectById(areaId).name
        def fullRegion = provinceName + " " + cityName + " " + areaName
        return fullRegion + " " + addressDO.address
    }

    /**
     * 订单列表
     * @param userId
     * @param showType
     * @param page
     * @param size
     * @return
     */
    @RequestMapping("/list")
    def list(@LoginUser Integer userId, @RequestParam("showType") Integer showType,
             @RequestParam(value = "page", defaultValue = "1") Integer page,
             @RequestParam(value = "size", defaultValue = "10") Integer size) {
        if (userId == null) {
            return HttpResponse.unlogin()
        }

        if (showType == null) {
            showType = 0
        }

        def orderStatus = OrderUtil.orderStatus(showType)
        def wrapper = new EntityWrapper<WxMallOrderDO>()
                .where("user_id = {0}", userId)
        orderStatus.forEach({
            status ->
                wrapper.and("order_status = {0}", status)
        })
        def orderList = orderService.selectList(wrapper)
        def count = orderList.size()
        def orderVOList = new LinkedList()
        orderList.forEach({
            order ->
                def orderVO = [:]
                orderVO.put("id", order.id)
                orderVO.put("orderSn", order.orderSn)
                orderVO.put("actualPrice", order.actualPrice)
                orderVO.put("orderStatusText", OrderUtil.orderStatusText(order))
                orderVO.put("handleOption", OrderUtil.build(order))

                def orderGoodsList = orderGoodsService.selectList(new EntityWrapper<WxMallOrderGoodsDO>()
                        .where("order_id = {0} and deleted = false", order.id))
                def orderGoodsVOList = new LinkedList()
                orderGoodsList.forEach({
                    orderGoods ->
                        def orderGoodsVO = [:]
                        orderGoodsVO.put("id", orderGoods.getId())
                        orderGoodsVO.put("goodsName", orderGoods.getGoodsName())
                        orderGoodsVO.put("number", orderGoods.getNumber())
                        orderGoodsVO.put("picUrl", orderGoods.getPicUrl())
                        orderGoodsVOList.add(orderGoodsVO)
                })
                orderVO.put("goodsList", orderGoodsVOList)
                orderVOList.add(orderVO)
        })
        def result = new HashMap()
        result.put("count", count)
        result.put("data", orderVOList)
        HttpResponse.success(result)
    }
    /**
     * 订单详情
     * @param userId
     * @param orderId
     * @return
     */
    @GetMapping("/detail")
    def detail(@LoginUser Integer userId, Integer orderId) {
        if (userId == null) {
            return HttpResponse.unlogin()
        }

        if (orderId == null) {
            return HttpResponse.badArgument()
        }

        def order = orderService.selectById(orderId)

        if (null == order) {
            return HttpResponse.fail(403, "订单不存在！")
        }
        if (order.userId != userId) {
            return HttpResponse.fail(403, "该订单不是当前用户订单！")
        }
        def orderVO = [:]
        orderVO.put("id", order.id)
        orderVO.put("orderSn", order.orderSn)
        orderVO.put("addTime", LocalDate.now())
        orderVO.put("consignee", order.consignee)
        orderVO.put("mobile", order.mobile)
        orderVO.put("address", order.address)
        orderVO.put("goodsPrice", order.goodsPrice)
        orderVO.put("freightPrice", order.freightPrice)
        orderVO.put("actualPrice", order.actualPrice)
        orderVO.put("orderStatusText", OrderUtil.orderStatusText(order))
        orderVO.put("handleOption", OrderUtil.build(order))
        def orderGoodsList = orderGoodsService.selectList(new EntityWrapper<WxMallOrderGoodsDO>()
                .where("order_id = {0} and deleted = false", order.id))
        def orderGoodsVOList = []
        orderGoodsList.forEach({
            orderGoods ->
                def orderGoodsVO = [:]
                orderGoodsVO.put("id", orderGoods.id)
                orderGoodsVO.put("orderId", orderGoods.orderId)
                orderGoodsVO.put("goodsId", orderGoods.goodsId)
                orderGoodsVO.put("goodsName", orderGoods.goodsName)
                orderGoodsVO.put("number", orderGoods.number)
                orderGoodsVO.put("retailPrice", orderGoods.retailPrice)
                orderGoodsVO.put("picUrl", orderGoods.picUrl)
                orderGoodsVO.put("goodsSpecificationValues", orderGoods.goodsSpecificationValues)
                orderGoodsVOList.add(orderGoodsVO)
        })
        def result = [:]
        result.put("orderInfo", orderVO)
        result.put("orderGoods", orderGoodsVOList)
        HttpResponse.success(result)
    }

    @PostMapping("/submit")
    def submit(@LoginUser Integer userId, @RequestBody String body) {
        if (userId == null) {
            return HttpResponse.unlogin()
        }

        if (body == null) {
            return HttpResponse.badArgument()
        }

        def cartId = JacksonUtil.parseInteger(body, "cartId")
        def addressId = JacksonUtil.parseInteger(body, "addressId")
        def couponId = JacksonUtil.parseInteger(body, "couponId")

        if (cartId == null || addressId == null || couponId == null) {
            return HttpResponse.badArgument()
        }

        def checkedAddress = addressService.selectById(addressId)
        // 获取可用的优惠券信息
        // 使用优惠券减免的金额
        def couponPrice = new BigDecimal(0.00)

        // 货品价格
        List<WxMallCartDO> checkedGoodsList
        if (cartId == 0) {
            checkedGoodsList = cartService.selectList(new EntityWrapper<WxMallCartDO>()
                    .where("user_id = {0} and checked = true and deleted = false", userId))
        } else {
            def cart = cartService.selectById(cartId)
            checkedGoodsList = []
            checkedGoodsList.add(cart)
        }

        if (checkedGoodsList.size() == 0) {
            return HttpResponse.badArgumentValue()
        }

        def checkedGoodsPrice = new BigDecimal(0.00 as char)
        checkedGoodsList.forEach({
            checkGoods ->
                checkedGoodsPrice = checkedGoodsPrice.add(checkGoods.retailPrice.multiply(new BigDecimal(checkGoods.number)))
        })
        // 根据订单商品总价计算运费，满88则免运费，否则8元；
        def freightPrice = new BigDecimal(0.00)
        if (checkedGoodsPrice < new BigDecimal(88.00)) {
            freightPrice = new BigDecimal(8.00)
        }
        // 可以使用的其他钱，例如用户积分
        def integralPrice = new BigDecimal(0.00)

        // 订单费用
        def orderTotalPrice = checkedGoodsPrice.add(freightPrice).subtract(couponPrice)
        def actualPrice = orderTotalPrice.subtract(integralPrice)

        // 开启事务管理
        DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition()
        defaultTransactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED)
        TransactionStatus status = txManager.getTransaction(defaultTransactionDefinition)
        def getOrder = orderService.selectOne(new EntityWrapper<WxMallOrderDO>().setSqlSelect(" max(id) as id"))
        def orderId
        if (getOrder == null) {
            orderId = 1
        } else {
            orderId = getOrder.id + 1
        }
        try {
            def order = new WxMallOrderDO()
            order.id = orderId
            order.userId = userId
            order.orderSn = orderService.generateOrderSn(userId)
            order.addTime = LocalDateTime.now()
            order.orderStatus = OrderUtil.STATUS_CREATE
            order.consignee = checkedAddress.name
            order.mobile = checkedAddress.mobile
            def detailedAddress = detailedAddress(checkedAddress)
            order.address = detailedAddress
            order.goodsPrice = checkedGoodsPrice
            order.freightPrice = freightPrice
            order.couponPrice = couponPrice
            order.integralPrice = integralPrice
            order.orderPrice = orderTotalPrice
            order.actualPrice = actualPrice
            orderService.insert(order)

            checkedGoodsList.forEach({
                cartGoods ->
                    def orderGoods = new WxMallOrderGoodsDO()
                    orderGoods.orderId = order.id
                    orderGoods.goodsId = cartGoods.goodsId
                    orderGoods.goodsSn = cartGoods.goodsSn
                    orderGoods.productId = cartGoods.productId
                    orderGoods.goodsName = cartGoods.goodsName
                    orderGoods.picUrl = cartGoods.picUrl
                    orderGoods.retailPrice = cartGoods.retailPrice
                    orderGoods.number = cartGoods.number
                    orderGoods.goodsSpecificationIds = JSONUtils.toJSONString(cartGoods.goodsSpecificationIds)
                    orderGoods.goodsSpecificationValues = cartGoods.goodsSpecificationValues
                    orderGoodsService.insert(orderGoods)
            })
            // 删除购物车里面的商品信息
            cartService.delete(new EntityWrapper<WxMallCartDO>()
                    .where("user_id = {0} and checked = true", userId))
            checkedGoodsList.forEach({
                checkGoods ->
                    def productId = checkGoods.productId
                    def product = productService.selectById(productId)

                    def remainNumber = product.goodsNumber - checkGoods.number

                    if (remainNumber < 0) {
                        throw new RuntimeException("库存不足！")
                    }
                    product.goodsNumber = remainNumber
                    productService.updateById(product)
            })

        } catch (Exception e) {
            e.printStackTrace()
            txManager.rollback(status)
            return HttpResponse.fail(403, "下单失败")
        }
        txManager.commit(status)
        def data = [:]
        data.put("orderId", orderId)
        return HttpResponse.success(data)
    }
    /**
     * 支付
     * @param userId
     * @param body
     * @return
     */
    @RequestMapping("/pay")
    def payPrePay(@LoginUser Integer userId, @RequestBody String body) {
        if (userId == null) {
            return HttpResponse.unlogin()
        }
        Integer orderId = JacksonUtil.parseInteger(body, "orderId")
        if (orderId == null) {
            return HttpResponse.badArgument()
        }

        def order = orderService.selectById(orderId)

        if (order == null) {
            return HttpResponse.badArgumentValue()
        }

        if (order.userId != userId) {
            return HttpResponse.fail(403, "该订单不属于该用户！")
        }

        def handleOption = OrderUtil.build(order)
        if (!handleOption.isPay()) {
            return HttpResponse.fail(403, "订单不能支付")
        }

        order.payStatus = OrderUtil.STATUS_PAY
        def order1 = new WxMallOrderDO()
        order1.id = order.id
        order1.orderStatus = (Short) OrderUtil.STATUS_PAY
        orderService.updateById(order1)
        return HttpResponse.success("付款成功！（模拟）")
    }
    /**
     * 取消订单
     * @param userId
     * @param body
     */
    @PostMapping("/cancel")
    def cancel(@LoginUser Integer userId, @RequestBody String body) {
        if (userId == null) {
            return HttpResponse.unlogin()
        }
        Integer orderId = JacksonUtil.parseInteger(body, "orderId")
        if (orderId == null) {
            return HttpResponse.badArgument()
        }

        def order = orderService.selectById(orderId)

        if (order == null) {
            return HttpResponse.badArgumentValue()
        }
        if (order.userId != userId) {
            return HttpResponse.badArgumentValue()
        }

        // 检测是否能够取消
        def handleOption = OrderUtil.build(order)
        if (!handleOption.isCancel()) {
            return HttpResponse.fail(403, "订单不能取消")
        }

        // 开启事务管理
        def definition = new DefaultTransactionDefinition()
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED)
        def status = txManager.getTransaction(definition)

        try {
            // 设置订单已取消状态
            order.orderStatus = OrderUtil.STATUS_CANCEL
            order.setEndTime(LocalDateTime.now())
            orderService.updateById(order)

            // 商品货品数量增加
            List<WxMallOrderGoodsDO> orderGoodsList = orderGoodsService.selectList(new EntityWrapper<WxMallOrderGoodsDO>()
                    .where("order_id = {0} and deleted = false", orderId))

            for (def orderGoods : orderGoodsList) {
                Integer productId = orderGoods.getProductId()
                WxMallProductDO product = productService.selectById(productId)
                Integer number = product.goodsNumber + orderGoods.number
                product.goodsNumber = number
                productService.updateById(product)
            }
        } catch (Exception ex) {
            txManager.rollback(status)
            ex.printStackTrace()
            return HttpResponse.fail(403, "订单取消失败")
        }
        txManager.commit(status)
        HttpResponse.success()
    }

    /**
     * 发货
     * 1. 检测当前订单是否能够发货
     * 2. 设置订单发货状态
     *
     * @param userId 用户ID
     * @param body 订单信息，{ orderId：xxx, shipSn: xxx, shipChannel: xxx }* @return 订单操作结果
     * 成功则 { errno: 0, errmsg: '成功' }* 失败则 { errno: XXX, errmsg: XXX }
     */
    @PostMapping("/ship")
    def ship(@LoginUser Integer userId, @RequestBody String body) {
        if (userId == null) {
            return HttpResponse.unlogin()
        }
        Integer orderId = JacksonUtil.parseInteger(body, "orderId")
        String shipSn = JacksonUtil.parseString(body, "shipSn")
        String shipChannel = JacksonUtil.parseString(body, "shipChannel")
        if (orderId == null || shipSn == null || shipChannel == null) {
            return HttpResponse.badArgument()
        }

        def order = orderService.selectById(orderId)
        if (order == null) {
            return HttpResponse.badArgument()
        }
        if (order.userId != userId) {
            return HttpResponse.badArgumentValue()
        }

        // 如果订单不是已付款状态，则不能发货
        if (order.orderStatus != OrderUtil.STATUS_PAY) {
            return HttpResponse.fail(403, "订单不能确认收货")
        }

        order.orderStatus = OrderUtil.STATUS_SHIP
        order.shipSn = shipSn
        order.shipChannel = shipChannel
        order.shipStartTime = LocalDateTime.now()
        orderService.updateById(order)

        HttpResponse.success()
    }

    /**
     * 确认收货
     * 1. 检测当前订单是否能够确认订单
     * 2. 设置订单确认状态
     *
     * @param userId 用户ID
     * @param body 订单信息，{ orderId：xxx }* @return 订单操作结果
     * 成功则 { errno: 0, errmsg: '成功' }* 失败则 { errno: XXX, errmsg: XXX }
     */
    @PostMapping("confirm")
    def confirm(@LoginUser Integer userId, @RequestBody String body) {
        if (userId == null) {
            return HttpResponse.unlogin()
        }
        def orderId = JacksonUtil.parseInteger(body, "orderId")
        if (orderId == null) {
            return HttpResponse.badArgument()
        }

        def order = orderService.selectById(orderId)
        if (order == null) {
            return HttpResponse.badArgument()
        }
        if (order.userId != userId) {
            return HttpResponse.badArgumentValue()
        }

        def handleOption = OrderUtil.build(order)
        if (!handleOption.isConfirm()) {
            return HttpResponse.fail(403, "订单不能确认收货");
        }

        order.orderStatus = OrderUtil.STATUS_CONFIRM
        order.confirmTime = LocalDateTime.now()
        orderService.updateById(order)
        HttpResponse.success()
    }

    @PostMapping("/delete")
    def delete(@LoginUser Integer userId, @RequestBody String body) {
        if (userId == null) {
            return HttpResponse.unlogin()
        }
        def orderId = JacksonUtil.parseInteger(body, "orderId")
        if (orderId == null) {
            return HttpResponse.badArgument()
        }

        def order = orderService.selectById(orderId)
        if (order == null) {
            return HttpResponse.badArgument()
        }
        if (order.userId != userId) {
            return HttpResponse.badArgumentValue();
        }

        def handleOption = OrderUtil.build(order)
        if (!handleOption.isDelete()) {
            return HttpResponse.fail(403, "订单不能删除")
        }

        // 订单order_status没有字段用于标识删除
        // 而是存在专门的delete字段表示是否删除
        orderService.deleteById(orderId)

        return HttpResponse.success()
    }
    /**可以评价的订单商品信息
     * @param userId
     * @param orderId
     * @param goodsId
     */
    @GetMapping("/comment")
    def comment(@LoginUser Integer userId, Integer orderId, Integer goodsId) {
        if (userId == null) {
            return HttpResponse.unlogin()
        }
        if (orderId == null) {
            return HttpResponse.badArgument()
        }

        def orderGoodsList = orderGoodsService.selectList(new EntityWrapper<WxMallOrderGoodsDO>()
                .where("order_id = {0} and goods_id = {1} and deleted = false", orderId, goodsId))
        def size = orderGoodsList.size()

        Assert.state(size < 2, "存在多个符合条件的订单商品")

        if (size == 0) {
            return HttpResponse.badArgumentValue()
        }

        def orderGoods = orderGoodsList.get(0)
        return HttpResponse.success(orderGoods)
    }
}
