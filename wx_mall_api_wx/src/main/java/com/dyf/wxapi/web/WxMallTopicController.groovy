package com.dyf.wxapi.web

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.baomidou.mybatisplus.plugins.Page
import com.dyf.core.utils.HttpResponse
import com.dyf.db.domain.WxMallTopicDO
import com.dyf.db.service.WxMallTopicService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/wx/topic")
class WxMallTopicController {
    @Autowired
    private WxMallTopicService topicService

    /**
     * 专题列表
     *
     * @param page 分页页数
     * @param size 分页大小
     * @return 专题列表
     *   成功则
     *{
     *      errno: 0,
     *      errmsg: '成功',
     *      data:
     *{
     *              data: xxx,
     *              count: xxx
     *}*}*   失败则 { errno: XXX, errmsg: XXX }
     */
    @GetMapping("/list")
    def list(@RequestParam(value = "page", defaultValue = "1") Integer page,
             @RequestParam(value = "size", defaultValue = "10") Integer size) {

        def topicList = topicService.selectPage(new Page<WxMallTopicDO>(page, size), new EntityWrapper<WxMallTopicDO>()
                .where("deleted = false"))
        def total = topicService.selectCount(new EntityWrapper<WxMallTopicDO>()
                .where("deleted = false"))
        def data = [:]
        data.put("data", topicList)
        data.put("count", total)
        HttpResponse.success(data)
    }

    /**
     * 专题详情
     *
     * @param id 专题ID
     * @return 专题详情
     *   成功则
     *{
     *      errno: 0,
     *      errmsg: '成功',
     *      data: xxx
     *}*   失败则 { errno: XXX, errmsg: XXX }
     */
    @GetMapping("/detail")
    def detail(Integer id) {
        if (id == null) {
            return HttpResponse.badArgument()
        }

        def topic = topicService.selectById(id)
        return HttpResponse.success(topic)
    }

    /**
     * 相关专题
     *
     * @param id 专题ID
     * @return 相关专题
     *   成功则
     *{
     *      errno: 0,
     *      errmsg: '成功',
     *      data: xxx
     *}*   失败则 { errno: XXX, errmsg: XXX }
     */
    @GetMapping("/related")
    def related(Integer id) {
        if (id == null) {
            return HttpResponse.fail402()
        }

        def topicRelatedList = topicService.selectPage(new Page<WxMallTopicDO>(1, 4), new EntityWrapper<WxMallTopicDO>()
                .where("id = {0} and deleted =false", id)).getRecords()
        return HttpResponse.success(topicRelatedList)
    }
}
