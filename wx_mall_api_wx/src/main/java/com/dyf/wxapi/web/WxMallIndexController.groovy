package com.dyf.wxapi.web

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.baomidou.mybatisplus.plugins.Page
import com.dyf.core.utils.HttpResponse
import com.dyf.db.domain.WxMallAdDO
import com.dyf.db.domain.WxMallBrandDO
import com.dyf.db.domain.WxMallCategoryDO
import com.dyf.db.domain.WxMallGoodsDO
import com.dyf.db.domain.WxMallTopicDO
import com.dyf.db.service.WxMallAdService
import com.dyf.db.service.WxMallBrandService
import com.dyf.db.service.WxMallCategoryService
import com.dyf.db.service.WxMallGoodsService
import com.dyf.db.service.WxMallTopicService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/wx/home")
class WxMallIndexController {

    @Autowired
    private WxMallAdService adService
    @Autowired
    private WxMallGoodsService goodsService
    @Autowired
    private WxMallBrandService brandService
    @Autowired
    private WxMallTopicService topicService
    @Autowired
    private WxMallCategoryService categoryService


    @GetMapping("/index")
    def index() {
        def data = new HashMap<String, Object>()
        def banner = adService.selectList(new EntityWrapper<WxMallAdDO>()
                .where("position = {0} and deleted = false", 1))

        data.put("banner", banner)
        def channel = categoryService.selectList(new EntityWrapper<WxMallCategoryDO>()
                .where("level = {0} and deleted = false", "L1"))
        data.put("channel", channel)
        def newGoods = goodsService.selectPage(new Page<WxMallGoodsDO>(1, 4), new EntityWrapper<WxMallGoodsDO>()
                .where("is_new = true and deleted =false")).getRecords()
        data.put("newGoodsList", newGoods)

        def hotGoods = goodsService.selectPage(new Page<WxMallGoodsDO>(1, 4), new EntityWrapper<WxMallGoodsDO>()
                .where("is_hot = true and deleted = false")).getRecords()
        data.put("hotGoodsList", hotGoods)


        def brandList = brandService.selectPage(new Page<WxMallBrandDO>(1, 4), new EntityWrapper<WxMallBrandDO>()
                .where("deleted =false")).getRecords()

        data.put("brandList", brandList)

        def topicList = topicService.selectPage(new Page<WxMallTopicDO>(1, 3), new EntityWrapper<WxMallTopicDO>()
                .where("deleted = false ")).getRecords()

        data.put("topicList", topicList)

        def categoryList = new LinkedList()

        def catL1List = categoryService.selectPage(new Page<WxMallCategoryDO>(1, 6), new EntityWrapper<WxMallCategoryDO>()
                .where("level = 'L1' and name != '推荐' AND deleted =false")).getRecords()

        catL1List.forEach({
            cateL1 ->
                def catL2List = categoryService.selectList(new EntityWrapper<WxMallCategoryDO>()
                        .where("parent_id = {0} and deleted =false", cateL1.id))
                def l2List = new LinkedList()

                catL2List.forEach({
                    catL2 ->
                        l2List.add(catL2.id)
                })

//                def categoryGoods = goodsService.selectPage(new Page<WxMallGoodsDO>(1, 5), new EntityWrapper<WxMallGoodsDO>()
//                        .where("category_id in {0} and deleted = false", l2List)).getRecords()


                def catGoods = [:]
                catGoods.put("id", cateL1.id)
                catGoods.put("name", cateL1.name)
                catGoods.put("goodsList", [])
        })

        data.put("floorGoodsList", categoryList)
        HttpResponse.success(data)
    }
}
