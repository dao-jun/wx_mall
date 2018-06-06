package com.dyf.wxapi.web

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.dyf.core.utils.HttpResponse
import com.dyf.db.domain.WxMallBrandDO
import com.dyf.db.service.WxMallBrandService
import com.github.pagehelper.PageHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/wx/brand")
class WxMallBrandController {

    @Autowired
    private WxMallBrandService brandService
    /**
     * 品牌列表
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list")
    def list(@RequestParam(value = "page", defaultValue = "1") Integer page,
             @RequestParam(value = "size", defaultValue = "10") Integer size) {
        def wrapper = new EntityWrapper<WxMallBrandDO>()
        wrapper.where("deleted = {0}", false)
        def pageInfo = PageHelper.startPage(page, size).doSelectPageInfo({
            ->
            brandService.selectList(wrapper)
        })

        def total = brandService.selectCount(wrapper)
        def data = new HashMap()
        data.put("brandList", pageInfo.getList())
        data.put("totalPages", (int) Math.ceil((double) total / size))
        HttpResponse.success(data)
    }

    @GetMapping("/detail")
    def detail(Integer id) {
        if (id == null) {
             return HttpResponse.badArgument()
        }
        def entity = brandService.selectOne(new EntityWrapper<WxMallBrandDO>().where("id = {0}", id))
        if (entity == null) {
           return HttpResponse.badArgumentValue()
        }

        def data = new HashMap()
        data.put("brand", entity)
        HttpResponse.success(data)
    }

}
