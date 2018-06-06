package com.dyf.wxapi.web

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.dyf.core.utils.HttpResponse
import com.dyf.db.domain.WxMallRegionDO
import com.dyf.db.service.WxMallRegionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/wx/region")
class WxMallRegionController {

    @Autowired
    private WxMallRegionService regionService

    /**
     * 区域数据
     *
     * 根据父区域ID，返回子区域数据。
     * 如果父区域ID是0，则返回省级区域数据；
     *
     * @param pid 父区域ID
     * @return 区域数据
     *   成功则
     *{
     *      errno: 0,
     *      errmsg: '成功',
     *      data: xxx
     *}*   失败则 { errno: XXX, errmsg: XXX }
     */
    @GetMapping("list")
    def list(Integer pid) {
        if (pid == null) {
            return HttpResponse.badArgument()
        }

        def regionList = regionService.selectList(new EntityWrapper<WxMallRegionDO>()
                .where("pid = {0}", pid))
        return HttpResponse.success(regionList)
    }
}
