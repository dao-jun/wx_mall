package com.dyf.apiadmin.web

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.baomidou.mybatisplus.plugins.Page
import com.dyf.apiadmin.annotions.LoginAdmin
import com.dyf.core.utils.HttpResponse
import com.dyf.db.domain.WxMallIssueDO
import com.dyf.db.service.WxMallIssueService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import javax.swing.text.html.parser.Entity

@RestController
@RequestMapping("/admin/issue")
class AdminIssueController {
    @Autowired
    private WxMallIssueService issueService

    @GetMapping("/list")
    def list(@LoginAdmin Integer adminId,
             String question,
             @RequestParam(value = "page", defaultValue = "1") Integer page,
             @RequestParam(value = "limit", defaultValue = "10") Integer limit,
             String sort, String order) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        def wrapper = new EntityWrapper<WxMallIssueDO>()

        if (!StringUtils.isEmpty(question)) {
            wrapper.where("question like {0}", "%" + question + "%")
        }
        wrapper.where("deleted = false")
        def issueList = issueService.selectPage(new Page<WxMallIssueDO>(page, limit), wrapper).getRecords()
        int total = issueService.selectCount(wrapper)
        def data = [:]
        data.put("total", total)
        data.put("items", issueList)

        HttpResponse.success(data)
    }

    @PostMapping("/create")
    def create(@LoginAdmin Integer adminId, @RequestBody WxMallIssueDO brand) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        issueService.insert(brand)
        HttpResponse.success(brand)
    }

    @GetMapping("/read")
    def read(@LoginAdmin Integer adminId, Integer id) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }

        if (id == null) {
            return HttpResponse.badArgument()
        }

        def brand = issueService.selectById(id)
        return HttpResponse.success(brand)
    }

    @PostMapping("/update")
    def update(@LoginAdmin Integer adminId, @RequestBody WxMallIssueDO brand) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        issueService.updateById(brand)
        HttpResponse.success(brand)
    }

    @PostMapping("/delete")
    def delete(@LoginAdmin Integer adminId, @RequestBody WxMallIssueDO brand) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        issueService.deleteById(brand.id)
        HttpResponse.success()
    }

}
