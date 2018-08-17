package com.dyf.wxapi.web

import com.alibaba.druid.support.json.JSONUtils
import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.dyf.core.utils.JacksonUtil
import com.dyf.core.utils.HttpResponse
import com.dyf.db.domain.WxMallAddressDO
import com.dyf.db.domain.WxMallCartDO
import com.dyf.db.domain.WxMallGoodsDO
import com.dyf.db.domain.WxMallGoodsSpecificationDO
import com.dyf.db.domain.WxMallProductDO
import com.dyf.db.service.WxMallAddressService
import com.dyf.db.service.WxMallCartService
import com.dyf.db.service.WxMallGoodsService
import com.dyf.db.service.WxMallGoodsSpecificationService
import com.dyf.db.service.WxMallProductService
import com.dyf.wxapi.annotation.LoginUser
import jdk.nashorn.internal.runtime.JSONListAdapter
import org.apache.commons.lang3.ObjectUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.configurationprocessor.json.JSONArray
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import java.util.stream.Collectors

@RestController
@RequestMapping("/wx/cart")
class WxMallCartController {

    @Autowired
    private WxMallCartService cartService
    @Autowired
    private WxMallGoodsService goodsService
    @Autowired
    private WxMallProductService productService
    @Autowired
    private WxMallGoodsSpecificationService specificationService
    @Autowired
    private WxMallAddressService addressService
    /**
     * 用户购物车
     * @param userId
     * @return
     */
    @GetMapping("/index")
    def index(@LoginUser Integer userId) {
        if (userId == null) {
            return HttpResponse.unlogin()
        }
        def wrapper = new EntityWrapper<WxMallCartDO>()
        wrapper.where("user_id = {0}", userId).and("deleted = {0}", false)
        def cartList = cartService.selectList(wrapper)
        def goodsCount = 0
        def goodsAmount = new BigDecimal(0.00)
        def checkedGoodsCount = 0
        def checkedGoodsAmount = new BigDecimal(0.00)
        cartList.forEach({
            cart ->
                checkedGoodsCount += cart.number
                checkedGoodsAmount = goodsAmount.add(cart.retailPrice * new BigDecimal(cart.number))
        })
        def cartTotal = [:]
        cartTotal.put("goodsCount", goodsCount)
        cartTotal.put("goodsAmount", goodsAmount)
        cartTotal.put("checkedGoodsCount", checkedGoodsCount)
        cartTotal.put("checkedGoodsAmount", checkedGoodsAmount)

        def result = [:]
        result.put("cartList", cartList)
        result.put("cartTotal", cartTotal)

        HttpResponse.success(result)
    }

    /**
     * 添加商品到购物车
     * @param userId
     * @param cart
     */
    @PostMapping("/add")
    def add(@LoginUser Integer userId, @RequestBody WxMallCartDO cart) {
        if (userId == null) {
            return HttpResponse.unlogin()
        }

        if (cart == null) {
            println("================")
            return HttpResponse.badArgument()
        }

        def productId = cart.productId
        def number = cart.number.intValue()
        def goodsId = cart.goodsId
        if (!ObjectUtils.allNotNull(productId, number, goodsId)) {
            println "----------------"
            return HttpResponse.badArgument()
        }

        //商品是否可购买
        def goods = goodsService.selectOne(new EntityWrapper<WxMallGoodsDO>().where("id = {0}", goodsId).and("deleted = {0}", false))
        if (goods == null || !goods.isOnSale) {
            return HttpResponse.fail(400, "该商品已经下架！")
        }

        def wrapper = new EntityWrapper<WxMallProductDO>()
        wrapper.where("id = {0}", productId).and("deleted = {0}", false)
        def product = productService.selectOne(wrapper)
        def wrapper1 = new EntityWrapper<WxMallCartDO>()
        wrapper1.where("goods_id = {0} and product_id = {1} and user_id = {2}", goodsId, productId, userId)
        //判断购物车中是否存在此规格商品
        def existCart = cartService.selectOne(wrapper1)
        if (existCart == null) {
            //取得规格的信息,判断规格库存
            if (product == null || number > product.getGoodsNumber()) {
                return HttpResponse.fail(400, "库存不足")
            }
            def ids
            try {
                ids = new String(product.goodsSpecificationIds.getBytes("UTF-8"), "UTF-8")
            } catch (Exception e) {
                return HttpResponse.badArgumentValue()
            }
            ids = ids.replaceAll('\\[', "").replaceAll('\\]', "").split(",")
            ids = Arrays.stream(ids).map({
                obj ->
                    return Integer.valueOf((String) obj)
            }).collect(Collectors.toList())
            def goodsSpecificationValue = " "
            for (def id : ids) {
                def goodsSpecification = specificationService.selectById(id)
                if (goodsSpecification == null || goodsSpecification.goodsId != goodsId) {
                    return HttpResponse.badArgument()
                }

                if (goodsSpecificationValue == null) {
                    goodsSpecificationValue = goodsSpecification.value
                } else {
                    goodsSpecificationValue = " " + goodsSpecificationValue + " " + goodsSpecification.value
                }
            }
            cart.id = null
            cart.goodsSn = goods.goodsSn
            cart.goodsName = goods.name
            cart.picUrl = goods.primaryPicUrl
            cart.retailPrice = goods.retailPrice
            cart.goodsSpecificationIds = JSONUtils.toJSONString(product.goodsSpecificationIds)
            cart.goodsSpecificationValues = goodsSpecificationValue
            cart.userId = userId
            cart.checked = true
            cartService.insert(cart)
        } else {
            def num = existCart.number + number
            if (num > product.goodsNumber) {
                return HttpResponse.fail(400, "库存不足")
            }
            existCart.number = num
            cartService.update(existCart, new EntityWrapper<WxMallCartDO>().where("id = {0}", existCart.id))
        }
        goodsCount(userId)
    }

    /**
     * 快速购买
     * @param userId
     * @param cart
     */
    @PostMapping("/fastadd")
    def fastAdd(@LoginUser Integer userId, @RequestBody WxMallCartDO cart) {
        if (userId == null) {
            return HttpResponse.unlogin()
        }
        if (cart == null) {
            return HttpResponse.badArgument()
        }

        def productId = cart.productId
        def number = cart.number.intValue()
        def goodsId = cart.goodsId
        if (!ObjectUtils.allNotNull(productId, number, goodsId)) {
            return HttpResponse.badArgument()
        }
        //判断商品是否可以购买
        def goods = goodsService.selectOne(new EntityWrapper<WxMallGoodsDO>().where("id = {0}", goodsId))
        if (goods == null || !goods.isOnSale) {
            return HttpResponse.fail(400, "商品已下架")
        }

        def product = productService.selectOne(new EntityWrapper<WxMallProductDO>().where("id = {0}", productId))
        WxMallCartDO existCart = cartService.selectOne(new EntityWrapper<WxMallCartDO>().where("goods_id={0} and product_id  ={1} and user_id = {2}", goodsId, productId, userId))
        if (existCart == null) {
            if (product == null || number > product.goodsNumber) {
                return HttpResponse.fail(400, "库存不足")
            }
            def ids = product.goodsSpecificationIds
            def goodsSpecificationValue = null
            ids = (List<Integer>) JSONUtils.parse(ids)
            for (def id in ids) {
                def goodsSpecification = specificationService.selectOne(new EntityWrapper<WxMallGoodsSpecificationDO>().where("id = {0}", id).and("deleted= {0}", false))
                if (goodsSpecification == null || !goodsSpecification.goodsId.equals(goodsId)) {
                    return HttpResponse.badArgument()
                }
                if (goodsSpecificationValue == null) {
                    goodsSpecificationValue = goodsSpecification.value
                } else {
                    goodsSpecificationValue = goodsSpecificationValue + " " + goodsSpecification.value
                }
            }

            cart.id = null
            cart.goodsSn = goods.goodsSn
            cart.goodsName = goods.name
            cart.picUrl = goods.primaryPicUrl
            cart.retailPrice = product.retailPrice
            cart.goodsSpecificationIds = product.goodsSpecificationIds
            cart.goodsSpecificationValues = goodsSpecificationValue
            cart.userId = userId
            cart.checked = true
            cartService.insert(cart)
        } else {
            def num = number
            if (num > product.goodsNumber) {
                return HttpResponse.fail(400, "库存不足")
            }
            existCart.number = num
            cartService.update(existCart, new EntityWrapper<WxMallCartDO>().where("id = {0}", existCart.id))
        }
        HttpResponse.success(existCart != null ? existCart.getId() : cart.getId())
    }
    /**
     * 更新指定的购物车信息
     * @param userId
     * @param cart
     */
    @PostMapping("/update")
    def update(@LoginUser Integer userId, @RequestBody WxMallCartDO cart) {
        if (userId == null) {
            return HttpResponse.unlogin()
        }
        if (cart == null) {
            return HttpResponse.badArgument()
        }

        def productId = cart.productId
        def number = cart.number.intValue()
        def goodsId = cart.goodsId
        def id = cart.id
        if (!ObjectUtils.allNotNull(id, productId, number, goodsId)) {
            return HttpResponse.badArgument()
        }
        //判断是否存在该订单
        // 如果不存在，直接返回错误
        WxMallCartDO existCart = cartService.selectOne(new EntityWrapper<WxMallCartDO>().where("id = {0}", id).and("deleted = {0}", false))
        if (existCart == null) {
            return HttpResponse.badArgumentValue()
        }

        // 判断goodsId和productId是否与当前cart里的值一致
        if (existCart.goodsId != goodsId) {
            return HttpResponse.badArgumentValue()
        }
        if (existCart.productId != productId) {
            return HttpResponse.badArgumentValue()
        }

        //判断商品是否可以购买
        def goods = goodsService.selectOne(new EntityWrapper<WxMallGoodsDO>().where("goods_id = {0}", goodsId).and("deleted = {0}", false))
        println(goods.toString() + goods.isOnSale)
        if (goods == null || !goods.isOnSale) {
            return HttpResponse.fail(403, "商品已下架")
        }

        //取得规格的信息,判断规格库存
        def product = productService.selectOne(new EntityWrapper<WxMallProductDO>().where("id = {0}", productId).and("deleted = {0}", false))
        if (product == null || product.goodsNumber < number) {
            return HttpResponse.fail(403, "库存不足")
        }
        existCart.number = number.shortValue()
        cartService.update(existCart, new EntityWrapper<WxMallCartDO>().where("id = {0}", existCart.id))
        HttpResponse.success()
    }
    /**
     * 购物车商品勾选
     * 如果原来没有勾选，则设置勾选状态；如果商品已经勾选，则设置非勾选状态。
     * @param userId
     * @param body
     */
    @PostMapping("/checked")
    def checked(@LoginUser Integer userId, @RequestBody String body) {
        if (userId == null) {
            return HttpResponse.unlogin()
        }

        if (body == null) {
            return HttpResponse.badArgument()
        }

        def productIds = JacksonUtil.parseIntegerList(body, "productIds")
        if (productIds == null) {
            return HttpResponse.badArgument()
        }

        def checkValue = JacksonUtil.parseInteger(body, "isChecked")
        if (checkValue == null) {
            return HttpResponse.badArgument()
        }

        def isChecked = (checkValue.intValue() == 1)

        productIds.forEach({
            productId ->
                def cart = new WxMallCartDO()
                cart.userId = userId
                cart.productId = Integer.valueOf(productId)
                cart.checked = isChecked
                cartService.update(cart, new EntityWrapper<WxMallCartDO>().where("user_id ={0}", userId).and("product_id = {0}", productId))
        })

        return index(userId)
    }

    @PostMapping("/delete")
    def delete(@LoginUser Integer userId, @RequestBody String body) {
        if (userId == null) {
            return HttpResponse.unlogin()
        }

        if (body == null) {
            return HttpResponse.badArgument()
        }

        def productIds = JacksonUtil.parseIntegerList(body, "productIds")

        if (productIds == null) {
            return HttpResponse.badArgument()
        }

        cartService.delete(new EntityWrapper<WxMallCartDO>().in("product_id", productIds).and("user_id = {0}", userId))
        return index(userId)
    }
    /**
     * 购物车商品数量
     * 如果用户没有登录，则返回空数据。
     * @param userId
     */
    @RequestMapping("/goodscount")
    def goodsCount(@LoginUser Integer userId) {
        if (userId == null) {
            return HttpResponse.success(0)
        }

        def goodsCount = 0
        def cartList = cartService.selectOne(new EntityWrapper<WxMallCartDO>().where("user_id = {0}", userId))

        for (def cart in cartList) {
            goodsCount += cart.number
        }

        return HttpResponse.success(goodsCount)
    }

    @GetMapping("/checkout")
    def checkout(@LoginUser Integer userId, Integer cartId, Integer addressId, Integer couponId) {
        if (userId == null) {
            return HttpResponse.unlogin()
        }

        def checkedAddress = null
        if (addressId == null || addressId == 0) {
            checkedAddress = addressService.selectOne(new EntityWrapper<WxMallAddressDO>().where("user_id = {0} and is_default = {1}", userId, true))
            // 如果仍然没有地址，则是没有收获地址
            // 返回一个空的地址id=0，这样前端则会提醒添加地址
            if (checkedAddress == null) {
                checkedAddress = new WxMallAddressDO()
                checkedAddress.id = 0
                addressId = 0
            } else {
                addressId = checkedAddress.id
            }
        } else {
            checkedAddress = addressService.selectOne(new EntityWrapper<WxMallAddressDO>().where("id = {0}", addressId))
            if (checkedAddress == null) {
                return HttpResponse.badArgumentValue()
            }
        }
        // 获取可用的优惠券信息
        // 使用优惠券减免的金额
        def couponPrice = new BigDecimal(0.00)
        // 商品价格
        def checkedGoodsList = null

        if (cartId == null || cartId == 0) {
            checkedGoodsList = cartService.selectList(new EntityWrapper<WxMallCartDO>().where("checked = true and user_id = {0} and deleted = false", userId))

        } else {
            def cart = cartService.selectOne(new EntityWrapper<WxMallCartDO>().where("id = {0} and deleted = false", cartId))
            if (cart == null) {
                return HttpResponse.badArgumentValue()
            }
            checkedGoodsList = []
            checkedGoodsList.add(cart)
        }
        def checkedGoodsPrice = new BigDecimal(0.00)

        checkedGoodsList.each {
            WxMallCartDO cart1 ->
                checkedGoodsPrice = checkedGoodsPrice.add(cart1.retailPrice.multiply(new BigDecimal(cart1.number)))
        }
        // 根据订单商品总价计算运费，满88则免运费，否则8元；
        def freightPrice = new BigDecimal(0.00);
        if (checkedGoodsPrice.compareTo(new BigDecimal(88.00)) == -1) {
            freightPrice = new BigDecimal(8.00)
        }
        // 可以使用的其他钱，例如用户积分
        def integralPrice = new BigDecimal(0.00)
        // 订单费用
        def orderTotalPrice = checkedGoodsPrice.add(freightPrice).subtract(couponPrice);
        def actualPrice = orderTotalPrice.subtract(integralPrice);
        def data = [:]
        data.put("addressId", addressId)
        data.put("checkedAddress", checkedAddress)
        data.put("couponId", couponId)
        data.put("checkedCoupon", 0)
        data.put("couponList", "")
        data.put("goodsTotalPrice", checkedGoodsPrice)
        data.put("freightPrice", freightPrice)
        data.put("couponPrice", couponPrice)
        data.put("orderTotalPrice", orderTotalPrice)
        data.put("actualPrice", actualPrice)
        data.put("checkedGoodsList", checkedGoodsList)
        HttpResponse.success(data)
    }
    /**
     * 商品优惠券列表
     * 目前不支持
     * @param userId
     * @return
     */
    @GetMapping("checkedCouponList")
    def checkedCouponList(@LoginUser Integer userId) {
        if (userId == null) {
            return HttpResponse.unlogin()
        }
        return HttpResponse.unsupport()
    }
}
