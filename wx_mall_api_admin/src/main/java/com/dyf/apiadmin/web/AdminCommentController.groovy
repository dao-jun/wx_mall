package com.dyf.apiadmin.web

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.baomidou.mybatisplus.plugins.Page
import com.dyf.apiadmin.annotions.LoginAdmin
import com.dyf.core.utils.HttpResponse
import com.dyf.db.domain.WxMallCommentDO
import com.dyf.db.service.WxMallCommentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/comment")
class AdminCommentController {
    @Autowired
    private WxMallCommentService commentService

    @GetMapping("/list")
    def list(@LoginAdmin Integer adminId,
             String userId, String valueId,
             @RequestParam(value = "page", defaultValue = "1") Integer page,
             @RequestParam(value = "limit", defaultValue = "10") Integer limit,
             String sort, String order) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        def wrapper = new EntityWrapper<WxMallCommentDO>()
        if (!StringUtils.isEmpty(userId)) {
            wrapper.where("user_id = {0}", userId)
        }
        if (!StringUtils.isEmpty(valueId)) {
            wrapper.where("value_id = {0}  and type_id = {1}", Integer.valueOf(valueId), (byte) 0)
        }
        wrapper.where("deleted = false")
        def brandList = commentService.selectPage(new Page<WxMallCommentDO>(page, limit), wrapper).getRecords()
        int total = commentService.selectCount(wrapper)
        def data = [:]
        data.put("total", total)
        data.put("items", brandList)

        HttpResponse.success(data)
    }

    @PostMapping("/create")
    def create(@LoginAdmin Integer adminId, @RequestBody WxMallCommentDO comment) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        commentService.insert(comment)
        HttpResponse.success(comment)
    }

    @GetMapping("/read")
    def read(@LoginAdmin Integer adminId, Integer id) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }

        if (id == null) {
            return HttpResponse.badArgument()
        }

        def comment = commentService.selectById(id)
        HttpResponse.success(comment)
    }

    @PostMapping("/update")
    def update(@LoginAdmin Integer adminId, @RequestBody WxMallCommentDO comment) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        commentService.updateById(comment)
        HttpResponse.success(comment)
    }

    @PostMapping("/delete")
    def delete(@LoginAdmin Integer adminId, @RequestBody WxMallCommentDO comment) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        commentService.deleteById(comment.id)
        HttpResponse.success()
    }

}
