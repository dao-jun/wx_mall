package com.dyf.wxapi.web

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.baomidou.mybatisplus.plugins.Page
import com.dyf.core.utils.JacksonUtil
import com.dyf.core.utils.HttpResponse
import com.dyf.db.domain.WxMallCollectDO
import com.dyf.db.domain.WxMallGoodsDO
import com.dyf.db.service.WxMallCollectService
import com.dyf.db.service.WxMallGoodsService
import com.dyf.wxapi.annotation.LoginUser
import com.github.pagehelper.PageHelper
import org.apache.commons.lang3.ObjectUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import java.time.LocalDateTime

@RestController
@RequestMapping("/wx/collect")
class WxMallCollectController {

    @Autowired
    private WxMallCollectService collectService
    @Autowired
    private WxMallGoodsService goodsService

    /**
     * 用户收藏列表
     *
     * @param userId
     * @param typeId
     * @param page
     * @param size
     * @return
     */
    @GetMapping("list")
    Object list(@LoginUser Integer userId, Integer typeId,
                @RequestParam(value = "page", defaultValue = "1") Integer page,
                @RequestParam(value = "size", defaultValue = "10") Integer size) {
        if (userId == null) {
            return HttpResponse.unlogin()
        }
        if (typeId == null) {
            return HttpResponse.badArgument()
        }

        def pageInfo = collectService.selectPage(new Page<WxMallCollectDO>(page, size), new EntityWrapper<WxMallCollectDO>()
                .where("user_id = {0} and type_id = {1} and deleted = false", userId, typeId))
        def collects = []
        pageInfo.getRecords().each {
            WxMallCollectDO collectDO ->
                Map<String, Object> c = new HashMap()
                c.put("id", collectDO.id)
                c.put("typeId", collectDO.typeId)
                c.put("valueId", collectDO.valueId)
                WxMallGoodsDO goodsDO = goodsService.selectById(collectDO.valueId)
                c.put("name", goodsDO.name)
                c.put("goodsBrief", goodsDO.goodsBrief)
                c.put("listPicUrl", goodsDO.listPicUrl)
                c.put("retailPrice", goodsDO.retailPrice)

                collects.add(c)
        }
        Map<String, Object> result = new HashMap()
        result.put("collectList", collects)
        result.put("totalPages", pageInfo.getTotal())
        HttpResponse.success(result)
    }
    /**
     *  用户收藏添加或删除
     * @param userId
     * @param body
     * @return
     */
    @PostMapping("addordelete")
    Object addordelete(@LoginUser Integer userId, @RequestBody String body) {
        if (userId == null) {
            return HttpResponse.unlogin()
        }
        if (body == null) {
            return HttpResponse.badArgument()
        }

        def typeId = JacksonUtil.parseInteger(body, "typeId")
        def valueId = JacksonUtil.parseInteger(body, "valueId")
        if (!ObjectUtils.allNotNull(typeId, valueId)) {
            return HttpResponse.badArgument()
        }

        def collectDO = collectService.selectOne(new EntityWrapper<WxMallCollectDO>()
                .where("user_id = {0} and type_id = {1} and value_id = {2} and deleted  = false", userId, typeId, valueId))
        def handleType

        if (collectDO != null) {
            handleType = "delete"
            collectService.deleteById(collectDO.id)
        } else {
            handleType = "add"
            collectDO = new WxMallCollectDO()
            collectDO.userId = userId
            collectDO.valueId = valueId
            collectDO.typeId = typeId
            collectDO.addTime = LocalDateTime.now()
            collectService.insert(collectDO)
        }

        Map<String, Object> data = new HashMap()
        data.put("type", handleType)
        HttpResponse.success(data)
    }

}
