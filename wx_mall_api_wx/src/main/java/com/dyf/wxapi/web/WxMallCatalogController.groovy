package com.dyf.wxapi.web

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.dyf.core.utils.HttpResponse
import com.dyf.db.domain.WxMallCategoryDO
import com.dyf.db.service.WxMallCategoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/wx/catalog")
class WxMallCatalogController {

    @Autowired
    private WxMallCategoryService categoryService

    /**
     * 分类栏目
     * @param id
     * @return
     */
    @GetMapping("/index")
    Object index(Integer id) {
        //所有一级分类目录
        List<WxMallCategoryDO> l1CatList = categoryService.selectList(new EntityWrapper<WxMallCategoryDO>()
                .where("level = {0} and deleted = false", "L1"))

        WxMallCategoryDO currentCate
        if (id != null) {
            currentCate = categoryService.selectById(id)
        } else {
            currentCate = l1CatList.get(0)
        }

        //当前一级目录的二级目录
        List<WxMallCategoryDO> level2
        if (null != currentCate) {
            level2 = categoryService.selectList(new EntityWrapper<WxMallCategoryDO>().where("parent_id = {0} and deleted = false", currentCate.id))
        }

        Map<String, Object> data = [:]
        data.put("categoryList", l1CatList)
        data.put("currentCategory", currentCate)
        data.put("currentSubCategory", level2)
        HttpResponse.success(data)
    }

    /**当前分类栏目
     *
     * @param id
     * @return
     */
    @GetMapping("/current")
    Object current(Integer id) {
        if (id == null) {
           return HttpResponse.badArgument()
        }

        //当前分类
        WxMallCategoryDO wxMallCategoryDO = categoryService.selectById(id)
        List<WxMallCategoryDO> wxMallCategoryDOList = categoryService
                .selectList(new EntityWrapper<WxMallCategoryDO>().where("parent_id = {0} and deleted = false", wxMallCategoryDO.id))
        Map<String, Object> data = new HashMap()
        data.put("currentCategory", wxMallCategoryDO)
        data.put("currentSubCategory", wxMallCategoryDOList)
        HttpResponse.success(data)
    }


}
