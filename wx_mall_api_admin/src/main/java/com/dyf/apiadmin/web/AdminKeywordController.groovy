package com.dyf.apiadmin.web

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.baomidou.mybatisplus.plugins.Page
import com.dyf.apiadmin.annotions.LoginAdmin
import com.dyf.core.utils.HttpResponse
import com.dyf.db.domain.WxMallKeywordDO
import com.dyf.db.service.WxMallKeywordService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/keyword")
class AdminKeywordController {
    @Autowired
    private WxMallKeywordService keywordService

    @GetMapping("/list")
    def list(@LoginAdmin Integer adminId,
             String keyword, String url,
             @RequestParam(value = "page", defaultValue = "1") Integer page,
             @RequestParam(value = "limit", defaultValue = "10") Integer limit,
             String sort, String order) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        def wrapper = new EntityWrapper<WxMallKeywordDO>()

        if (!StringUtils.isEmpty(keyword)) {
            wrapper.where("keyword like {0}", "%" + keyword + "%")
        }
        if (!StringUtils.isEmpty(url)) {
            wrapper.where("url like {0}", "%" + url + "%")
        }
        wrapper.where("deleted = false")
        def brandList = keywordService.selectPage(new Page<WxMallKeywordDO>(page, limit), wrapper).getRecords()
        int total = keywordService.selectCount(wrapper)
        def data = [:]
        data.put("total", total)
        data.put("items", brandList)

        HttpResponse.success(data)
    }

    @PostMapping("/create")
    def create(@LoginAdmin Integer adminId, @RequestBody WxMallKeywordDO keywords) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        keywordService.insert(keywords)
        HttpResponse.success(keywords)
    }

    @GetMapping("/read")
    def read(@LoginAdmin Integer adminId, Integer id) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }

        if (id == null) {
            return HttpResponse.badArgument()
        }

        def brand = keywordService.selectById(id)
        HttpResponse.success(brand)
    }

    @PostMapping("/update")
    def update(@LoginAdmin Integer adminId, @RequestBody WxMallKeywordDO keywords) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        keywordService.updateById(keywords)
        return HttpResponse.success(keywords)
    }

    @PostMapping("/delete")
    def delete(@LoginAdmin Integer adminId, @RequestBody WxMallKeywordDO brand) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        keywordService.deleteById(brand.id)
        return HttpResponse.success()
    }

}
