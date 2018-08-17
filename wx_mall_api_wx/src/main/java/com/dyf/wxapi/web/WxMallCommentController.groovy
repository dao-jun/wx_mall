package com.dyf.wxapi.web

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.baomidou.mybatisplus.plugins.Page
import com.dyf.core.utils.HttpResponse
import com.dyf.core.utils.JacksonUtil
import com.dyf.db.domain.WxMallCommentDO
import com.dyf.db.service.WxMallCommentService
import com.dyf.db.service.WxMallUserService
import com.dyf.wxapi.annotation.LoginUser
import com.github.pagehelper.PageHelper
import com.github.pagehelper.PageInfo
import org.apache.commons.lang3.ObjectUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import java.time.LocalDateTime
import java.util.stream.Collectors

@RestController
@RequestMapping("/wx/comment")
class WxMallCommentController {


    @Autowired
    private WxMallCommentService commentService

    @Autowired
    private WxMallUserService userService

    /**
     * 发表评论
     * @param userId
     * @param comment
     */
    @PostMapping("/post")
    def post(@LoginUser Integer userId, @RequestBody WxMallCommentDO comment) {
        if (userId == null) {
            return HttpResponse.unlogin()
        }

        if (comment == null) {
            return HttpResponse.badArgument()
        }

        comment.addTime = LocalDateTime.now()
        comment.userId = userId
        if (comment.picUrls.size() != 0) {

        } else {
            comment.picUrls = "[]"
        }
        commentService.insert(comment)
        HttpResponse.success(comment)
    }

    /**
     * 评论数量
     * @param typeId
     * @param valueId
     * @return
     */
    @GetMapping("/count")
    def count(Byte typeId, Integer valueId) {
        def allCount = commentService.selectCount(new EntityWrapper<WxMallCommentDO>()
                .where("value_id = {0} and type_id = {1} and deleted = false", valueId, typeId))

        def hasPicCount = commentService.selectCount(new EntityWrapper<WxMallCommentDO>()
                .where("value_id = {0} and type_id = {1} and has_picture = true and deleted = false", valueId, typeId))
        def data = [:]
        data.put("allCount", allCount)
        data.put("hasPicCount", hasPicCount)
        HttpResponse.success(data)
    }
    /**
     * 评论列表
     * @param typeId
     * @param valueId
     * @param showType
     * @param page
     * @param size
     */
    @GetMapping("/list")
    def list(Byte typeId, Integer valueId, Integer showType,
             @RequestParam(value = "page", defaultValue = "1") Integer page,
             @RequestParam(value = "size", defaultValue = "10") Integer size) {

        if (!ObjectUtils.allNotNull(typeId, valueId, showType)) {
            return HttpResponse.badArgument()
        }

        def wrapper = new EntityWrapper<WxMallCommentDO>()
        wrapper.where("value_id = {0} and type_id = {1} and deleted = false", valueId, typeId)
        if (showType == 1) {
            wrapper.and("has_picture = {0}", true)
        } else if (showType == 0) {
            wrapper.and("has_picture = {0}", false)
        }
        def pageInfo = commentService.selectPage(new Page<WxMallCommentDO>(page, size), wrapper)

        def commentVOList = []

        pageInfo.getRecords().forEach {
            obj ->
                if (obj.picUrls != null)
                    obj.picUrls = obj.picUrls.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll('\\"', "").split(",")
                else
                    obj.picUrls = []
        }

        pageInfo.getRecords().forEach({
            comment ->
                def commentVo = [:]
                def userInfo = userService.getInfo(comment.userId)
                commentVo.put("userInfo", userInfo)
                commentVo.put("addTime", comment.addTime)
                commentVo.put("content", comment.content)
                commentVo.put("picList", comment.picUrls)
                commentVOList.add(commentVo)
        })

        def data = [:]
        data.put("data", commentVOList)
        data.put("count", pageInfo.getTotal())
        data.put("currentPage", page)
        HttpResponse.success(data)
    }
}
