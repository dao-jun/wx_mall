package com.dyf.wxapi.web

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.baomidou.mybatisplus.plugins.Page
import com.dyf.core.utils.JacksonUtil
import com.dyf.core.utils.HttpResponse
import com.dyf.db.domain.WxMallFootPrintDO
import com.dyf.db.service.WxMallFootPrintService
import com.dyf.db.service.WxMallGoodsService
import com.dyf.wxapi.annotation.LoginUser
import com.github.pagehelper.PageHelper
import com.github.pagehelper.PageInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * create by dyf
 * 用户浏览历史
 */
@RestController
@RequestMapping("/wx/footprint")
class WxMallFootPrintController {
    @Autowired
    private WxMallFootPrintService footPrintService

    @Autowired
    private WxMallGoodsService goodsService
    /**
     * 用户删除足迹
     * @param userId
     * @param body
     */
    @PostMapping("/delete")
    def delete(@LoginUser Integer userId, @RequestBody String body) {
        if (userId == null) {
            return HttpResponse.unlogin()
        }

        if (body == null) {
            return HttpResponse.badArgument()
        }

        def footPrintId = JacksonUtil.parseInteger(body, "footprintId")
        if (footPrintId == null) {
            return HttpResponse.badArgument()
        }

        def footPrintDO = footPrintService.selectById(footPrintId)
        if (footPrintDO == null) {
            return HttpResponse.badArgumentValue()
        }
        if (footPrintDO.getUserId() != userId) {
            return HttpResponse.badArgumentValue()
        }

        footPrintService.deleteById(footPrintId)
        HttpResponse.success()
    }
    /**
     * 用户浏览历史列表
     * @param userId
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list")
    def list(@LoginUser Integer userId,
             @RequestParam(value = "page", defaultValue = "1") Integer page,
             @RequestParam(value = "size", defaultValue = "10") Integer size) {

        if (userId == null) {
            return HttpResponse.unlogin()
        }

        def pageInfo = footPrintService.selectPage(new Page<WxMallFootPrintDO>(page, size), new EntityWrapper<WxMallFootPrintDO>()
                .where("user_id = {0} and deleted = false", userId).orderBy("add_time", false))

        def count = pageInfo.getTotal()
        def total = (int) Math.ceil((double) count / size)

        def footPrintDTOList = new LinkedList<Object>()
        pageInfo.getRecords().forEach({
            footPrint ->
                def print = new HashMap()
                print.put("id", footPrint.id)
                print.put("goodsId", footPrint.goodsId)
                print.put("addTime", footPrint.addTime)

                def goods = goodsService.selectById(footPrint.goodsId)
                print.put("name", goods.name)
                print.put("goodsBrief", goods.goodsBrief)
                print.put("listPicUrl", goods.listPicUrl)
                print.put("retailPrice", goods.retailPrice)
                footPrintDTOList.add(print)
        })


        def data = new HashMap()
        data.put("footprintList", footPrintDTOList)
        data.put("totalPages", total)
        HttpResponse.success(data)
    }

}
