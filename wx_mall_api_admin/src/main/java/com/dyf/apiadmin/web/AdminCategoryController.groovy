package com.dyf.apiadmin.web

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.baomidou.mybatisplus.plugins.Page
import com.dyf.apiadmin.annotions.LoginAdmin
import com.dyf.core.utils.HttpResponse
import com.dyf.db.domain.WxMallCategoryDO
import com.dyf.db.service.WxMallCategoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/category")
class AdminCategoryController {

    @Autowired
    private WxMallCategoryService categoryService

    @GetMapping("/list")
    def list(@LoginAdmin Integer adminId,
             String id, String name,
             @RequestParam(value = "page", defaultValue = "1") Integer page,
             @RequestParam(value = "limit", defaultValue = "10") Integer limit,
             String sort, String order) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        def wrapper = new EntityWrapper<WxMallCategoryDO>()

        if (!StringUtils.isEmpty(id)) {
            wrapper.where("id  = {0}", Integer.valueOf(id))
        }
        if (!StringUtils.isEmpty(name)) {
            wrapper.where("name like {0}", "%" + name + "%")
        }
        wrapper.where("deleted = false")
        def collectList = categoryService.selectPage(new Page<WxMallCategoryDO>(page, limit)).getRecords()
        int total = categoryService.selectCount(wrapper)
        def data = [:]
        data.put("total", total)
        data.put("items", collectList)

        HttpResponse.success(data)
    }

    @PostMapping("/create")
    def create(@LoginAdmin Integer adminId, @RequestBody WxMallCategoryDO category) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        categoryService.insert(category)
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

        def category = categoryService.selectById(id)
        HttpResponse.success(category)
    }

    @PostMapping("/update")
    def update(@LoginAdmin Integer adminId, @RequestBody WxMallCategoryDO category) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        categoryService.updateById(category)
        HttpResponse.success()
    }

    @PostMapping("/delete")
    def delete(@LoginAdmin Integer adminId, @RequestBody WxMallCategoryDO category) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }
        categoryService.deleteById(category.id)
        HttpResponse.success()
    }

    @GetMapping("/l1")
    def catL1(@LoginAdmin Integer adminId) {
        if (adminId == null) {
            return HttpResponse.unlogin()
        }

        // 所有一级分类目录
        def l1CatList = categoryService.selectList(new EntityWrapper<WxMallCategoryDO>()
                .where("level = {0} and deleted = false", "L1"))
        def data = new HashMap<>(l1CatList.size())

        l1CatList.forEach {
            l1Cat ->
                data.put(l1Cat.id, l1Cat.name)
        }

        HttpResponse.success(data)
    }

}
