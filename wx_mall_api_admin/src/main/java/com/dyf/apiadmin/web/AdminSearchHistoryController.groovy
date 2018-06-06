package com.dyf.apiadmin.web

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.baomidou.mybatisplus.plugins.Page
import com.dyf.apiadmin.annotions.LoginAdmin
import com.dyf.core.utils.HttpResponse
import com.dyf.db.domain.WxMallSearchHistoryDO
import com.dyf.db.service.WxMallSearchHistoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/history")
class AdminSearchHistoryController {

    @Autowired
    private WxMallSearchHistoryService searchHistoryService

    @GetMapping("/list")
    def list(@LoginAdmin Integer adminId,
             String userId, String keyword,
             @RequestParam(value = "page", defaultValue = "1") Integer page,
             @RequestParam(value = "limit", defaultValue = "10") Integer limit,
             String sort, String order) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        def wrapper = new EntityWrapper<WxMallSearchHistoryDO>()
        if (!StringUtils.isEmpty(userId)) {
            wrapper.where("user_id = {0}", Integer.valueOf(userId))
        }
        if (!StringUtils.isEmpty(keyword)) {
            wrapper.where("keyword like {0}", "%" + keyword + "%")
        }
        wrapper.where("deleted = false")
        def footprintList = searchHistoryService.selectPage(new Page<WxMallSearchHistoryDO>(page, limit), wrapper).getRecords()
        def total = searchHistoryService.selectCount(wrapper)
        def data = [:]
        data.put("total", total)
        data.put("items", footprintList)

        HttpResponse.success(data)
    }

    @PostMapping("/create")
    def create(@LoginAdmin Integer adminId, @RequestBody WxMallSearchHistoryDO history) {
        if (adminId == null) {
            return HttpResponse.fail401()
        }
        HttpResponse.fail501()
    }

    @GetMapping("/read")
    def read(@LoginAdmin Integer adminId, Integer id) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }

        if (id == null) {
            return HttpResponse.badArgument()
        }

        def history = searchHistoryService.selectById(id)
        HttpResponse.success(history)
    }

    @PostMapping("/update")
    def update(@LoginAdmin Integer adminId, @RequestBody WxMallSearchHistoryDO history) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        searchHistoryService.updateById(history)
        HttpResponse.success()
    }

    @PostMapping("/delete")
    def delete(@LoginAdmin Integer adminId, @RequestBody WxMallSearchHistoryDO history) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        searchHistoryService.deleteById(history.id)
        HttpResponse.success()
    }
}
