package com.dyf.apiadmin.web

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.baomidou.mybatisplus.plugins.Page
import com.dyf.apiadmin.annotions.LoginAdmin
import com.dyf.core.utils.HttpResponse
import com.dyf.db.domain.WxMallTopicDO
import com.dyf.db.service.WxMallTopicService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/topic")
class AdminTopicController {
    @Autowired
    private WxMallTopicService topicService

    @GetMapping("/list")
    def list(@LoginAdmin Integer adminId,
             String title, String subtitle,
             @RequestParam(value = "page", defaultValue = "1") Integer page,
             @RequestParam(value = "limit", defaultValue = "10") Integer limit,
             String sort, String order) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }

        def wrapper = new EntityWrapper<WxMallTopicDO>()
        if (!StringUtils.isEmpty(title)) {
            wrapper.where("title like {0}", "%" + title + "%")
        }
        if (!StringUtils.isEmpty(subtitle)) {
            wrapper.where("subtitle = like {0}", "%" + subtitle + "%")
        }
        wrapper.where("deleted = false")
        def topicList = topicService.selectPage(new Page<WxMallTopicDO>(page, limit), wrapper).getRecords()
        int total = topicService.selectCount(wrapper)
        def data = [:]
        data.put("total", total)
        data.put("items", topicList)

        HttpResponse.success(data)
    }

    @PostMapping("/create")
    def create(@LoginAdmin Integer adminId, @RequestBody WxMallTopicDO topic) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        topicService.insert(topic)
        HttpResponse.success(topic)
    }

    @GetMapping("/read")
    def read(@LoginAdmin Integer adminId, Integer id) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }

        if (id == null) {
            return HttpResponse.badArgument()
        }

        def brand = topicService.selectById(id)
        HttpResponse.success(brand)
    }

    @PostMapping("/update")
    def update(@LoginAdmin Integer adminId, @RequestBody WxMallTopicDO topic) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        topicService.updateById(topic)
        HttpResponse.success(topic)
    }

    @PostMapping("/delete")
    def delete(@LoginAdmin Integer adminId, @RequestBody WxMallTopicDO topic) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        topicService.deleteById(topic.id)
        return HttpResponse.success()
    }

}
