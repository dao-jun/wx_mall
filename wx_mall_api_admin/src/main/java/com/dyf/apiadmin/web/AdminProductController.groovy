package com.dyf.apiadmin.web

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.baomidou.mybatisplus.plugins.Page
import com.dyf.apiadmin.annotions.LoginAdmin
import com.dyf.core.utils.HttpResponse
import com.dyf.db.domain.WxMallGoodsSpecificationDO
import com.dyf.db.domain.WxMallProductDO
import com.dyf.db.service.WxMallGoodsService
import com.dyf.db.service.WxMallGoodsSpecificationService
import com.dyf.db.service.WxMallProductService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/product")
class AdminProductController {
    @Autowired
    private WxMallProductService productService
    @Autowired
    private WxMallGoodsService goodsService
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
        def wrapper = new EntityWrapper<WxMallProductDO>()
        if (goodsId != null) {
            wrapper.where("goods_id = {0}", goodsId)
        }
        wrapper.where("deleted = false")
        def productList = productService.selectPage(new Page<WxMallProductDO>(page, limit), wrapper).getRecords()
        int total = productService.selectCount(wrapper)
        def data = [:]
        data.put("total", total)
        data.put("items", productList)

        HttpResponse.success(data)
    }

    /**
     *
     * @param adminId
     * @param litemallProduct
     * @return
     */
    @PostMapping("/create")
    def create(@LoginAdmin Integer adminId, @RequestBody WxMallProductDO litemallProduct) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }

        Integer goodsId = litemallProduct.getGoodsId()
        if (goodsId == null) {
            return HttpResponse.badArgument()
        }

        def goods = goodsService.selectById(goodsId)
        if (goods == null) {
            return HttpResponse.badArgumentValue()
        }

        def productList = productService.selectList(new EntityWrapper<WxMallProductDO>()
                .where("goods_id = {0} and deleted =false", goodsId))
        if (productList.size() != 0) {
            return HttpResponse.badArgumentValue()
        }

        Integer[] goodsSpecificationIds = (Integer[]) goodsSpecificationService.selectList(new EntityWrapper<WxMallGoodsSpecificationDO>()
                .where("goods_id = {0}", goodsId)).id.toArray()

        if (goodsSpecificationIds.length == 0) {
            return HttpResponse.serious()
        }

        def product = new WxMallProductDO()
        product.goodsId = goodsId
        product.goodsNumber = 0
        product.retailPrice = new BigDecimal(0.00)
        product.goodsSpecificationIds = goodsSpecificationIds
        productService.insert(product)

        HttpResponse.success()
    }

    @GetMapping("/read")
    def read(@LoginAdmin Integer adminId, Integer id) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }

        if (id == null) {
            return HttpResponse.badArgument()
        }

        def product = productService.selectById(id)
        HttpResponse.success(product)
    }

    @PostMapping("/update")
    def update(@LoginAdmin Integer adminId, @RequestBody WxMallProductDO product) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        productService.updateById(product)
        HttpResponse.success(product)
    }

    @PostMapping("/delete")
    def delete(@LoginAdmin Integer adminId, @RequestBody WxMallProductDO product) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        productService.deleteById(product.id)
        HttpResponse.success()
    }

}
