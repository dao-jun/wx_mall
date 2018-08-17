package com.dyf.wxapi.web

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.baomidou.mybatisplus.plugins.Page
import com.dyf.core.utils.HttpResponse
import com.dyf.db.domain.WxMallCategoryDO
import com.dyf.db.domain.WxMallCollectDO
import com.dyf.db.domain.WxMallCommentDO
import com.dyf.db.domain.WxMallFootPrintDO
import com.dyf.db.domain.WxMallGoodsAttributeDO
import com.dyf.db.domain.WxMallGoodsDO
import com.dyf.db.domain.WxMallIssueDO
import com.dyf.db.domain.WxMallProductDO
import com.dyf.db.domain.WxMallSearchHistoryDO
import com.dyf.db.service.WxMallBrandService
import com.dyf.db.service.WxMallCategoryService
import com.dyf.db.service.WxMallCollectService
import com.dyf.db.service.WxMallCommentService
import com.dyf.db.service.WxMallFootPrintService
import com.dyf.db.service.WxMallGoodsAttributeService
import com.dyf.db.service.WxMallGoodsService
import com.dyf.db.service.WxMallGoodsSpecificationService
import com.dyf.db.service.WxMallIssueService
import com.dyf.db.service.WxMallProductService
import com.dyf.db.service.WxMallSearchHistoryService
import com.dyf.db.service.WxMallUserService
import com.dyf.db.utils.SortUtil
import com.dyf.wxapi.annotation.LoginUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import java.time.LocalDateTime
import java.util.stream.Collectors

@RestController
@RequestMapping("/wx/goods")
class WxMallGoodsController {

    @Autowired
    private WxMallGoodsService goodsService
    @Autowired
    private WxMallProductService productService
    @Autowired
    private WxMallIssueService goodsIssueService
    @Autowired
    private WxMallGoodsAttributeService goodsAttributeService
    @Autowired
    private WxMallBrandService brandService
    @Autowired
    private WxMallCommentService commentService
    @Autowired
    private WxMallUserService userService
    @Autowired
    private WxMallCollectService collectService
    @Autowired
    private WxMallFootPrintService footprintService
    @Autowired
    private WxMallCategoryService categoryService
    @Autowired
    private WxMallSearchHistoryService searchHistoryService
    @Autowired
    private WxMallGoodsSpecificationService goodsSpecificationService

    /**
     * 商品详情
     * @param userId
     * @param id
     * @return
     */
    @GetMapping("/detail")
    def detail(@LoginUser Integer userId, Integer id) {
        if (id == null) {
            return HttpResponse.badArgument()
        }

        // 商品信息
        def info = goodsService.selectById(id)

        // 商品属性
        def goodsAttributeList = goodsAttributeService.selectList(new EntityWrapper<WxMallGoodsAttributeDO>()
                .where("goods_id = {0} and deleted = false", id))

        // 商品规格
        // 返回的是定制的GoodsSpecificationVo
        def specificationList = goodsSpecificationService.getSpecificationVoList(id)

        // 商品规格对应的数量和价格
        def productList = productService.selectList(new EntityWrapper<WxMallProductDO>()
                .where("goods_id = {0} and deleted = false", id))

        // 商品问题，这里是一些通用问题
        def issue = goodsIssueService.selectList(new EntityWrapper<WxMallIssueDO>()
                .where("deleted = false"))

        // 商品品牌商
        def brand = brandService.selectById(info.brandId)

        // 评论
        def comments = commentService.selectPage(new Page<WxMallCommentDO>(1, 2), new EntityWrapper<WxMallCommentDO>()
                .where("value_id = {0} and type_id = 0 and deleted = false", id).orderBy("add_time", false))
                .getRecords()

        def commentsVo = []
        int commentCount = commentService.selectCount(new EntityWrapper<WxMallCommentDO>()
                .where("value_id = {0} and type_id = 0 and deleted = false", id))
        comments.each {
            WxMallCommentDO comment ->
                if (comment.picUrls != null) {
                    comment.picUrls = comment.picUrls.replaceAll('\\[', "").replaceAll('\\]', "").replaceAll('\"', "").split(",")
                } else {
                    comment.picUrls = []
                }
                def c = [:]
                c.put("id", comment.id)
                c.put("addTime", comment.addTime)
                c.put("content", comment.content)
                def user = userService.selectById(comment.userId)
                c.put("nickname", user.nickname)
                c.put("avatar", user.avatar)
                c.put("picList", comment.picUrls)
                commentsVo.add(c)
        }
        def commentList = [:]
        commentList.put("count", commentCount)
        commentList.put("data", commentsVo)

        // 用户收藏
        def userHasCollect = 0
        if (userId != null) {
            userHasCollect = collectService.selectCount(new EntityWrapper<WxMallCollectDO>()
                    .where("user_id = {0} and value_id ={1} and deleted = false", userId, id))
        }

        // 记录用户的足迹
        if (userId != null) {
            def footprint = new WxMallFootPrintDO()
            footprint.addTime = LocalDateTime.now()
            footprint.userId = userId
            footprint.goodsId = id
            footprintService.insert(footprint)
        }

        def data = [:]
        data.put("info", info)
        data.put("userHasCollect", userHasCollect)
        data.put("issue", issue)
        data.put("comment", commentList)
        data.put("specificationList", specificationList)
        data.put("productList", productList)
        data.put("attribute", goodsAttributeList)
        data.put("brand", brand)

        HttpResponse.success(data)
    }

    @GetMapping("/category")
    def category(Integer id) {
        if (id == null) {
            return HttpResponse.badArgument()
        }
        def cur = categoryService.selectById(id)
        def parent
        List<WxMallCategoryDO> children

        if (cur.getParentId() == 0) {
            parent = cur
            children = categoryService.selectList(new EntityWrapper<WxMallCategoryDO>()
                    .where("parent_id = {0} and deleted = false", cur.id))
            cur = children.get(0)
        } else {
            parent = categoryService.selectById(cur.parentId)
            children = categoryService.selectList(new EntityWrapper<WxMallCategoryDO>()
                    .where("parent_id = {0} and deleted = false", cur.parentId))
        }
        def data = [:]
        data.put("currentCategory", cur)
        data.put("parentCategory", parent)
        data.put("brotherCategory", children)
        HttpResponse.success(data)
    }
    /**
     * 根据条件搜素商品
     *
     * 1. 这里的前五个参数都是可选的，甚至都是空
     * 2. 用户是可选登录，如果登录，则记录用户的搜索关键字
     *
     * @param categoryId 分类类目ID
     * @param brandId 品牌商ID
     * @param keyword 关键字
     * @param isNew 是否新品
     * @param isHot 是否热买
     * @param userId 用户ID
     * @param page 分页页数
     * @param size 分页大小
     * @param sort 排序方式
     * @param order 排序类型，顺序或者降序
     * @return 根据条件搜素的商品详情
     */
    @GetMapping("/list")
    def list(Integer categoryId, Integer brandId, String keyword, Integer isNew, Integer isHot,
             @LoginUser Integer userId,
             @RequestParam(value = "page", defaultValue = "1") Integer page,
             @RequestParam(value = "size", defaultValue = "10") Integer size,
             String sort, String order) {

        String sortWithOrder = SortUtil.goodsSort(sort, order)

        //添加到搜索历史
        if (userId != null && !(keyword == null || keyword == "")) {
            def searchHistoryVo = new WxMallSearchHistoryDO()
            searchHistoryVo.addTime = LocalDateTime.now()
            searchHistoryVo.keyword = keyword
            searchHistoryVo.userId = userId
            searchHistoryVo.from = "wx"
            searchHistoryService.insert(searchHistoryVo)
        }

        def wrapper = new EntityWrapper<WxMallGoodsDO>()
        if (categoryId != null) {
            wrapper.where("category_id = {0}", categoryId)
        }
        if (brandId != null) {
            wrapper.where("brand_id = {0}", brandId)
        }

        if (keyword != null) {
            wrapper.where("keywords like {0}", "%" + keyword + "%")
        }
        if (isHot != null) {
            wrapper.where("is_hot = {0}", isHot)
        }
        if (isNew != null) {
            wrapper.where("is_new = {0}", isNew)
        }
        def goodsList = goodsService.selectPage(new Page<WxMallGoodsDO>(page, size), wrapper).getRecords()

        // 查询商品所属类目列表。
        def wrapper1 = new EntityWrapper<WxMallGoodsDO>()
        if (brandId != null) {
            wrapper1.where("brand_id = {0}", brandId)
        }
        if (isNew != null) {
            wrapper1.where("is_new  = {0}", isNew.intValue())
        }
        if (isHot != null) {
            wrapper1.where("is_hot = {0}", isHot.intValue())
        }
        if (keyword != null) {
            wrapper1.where("brand_id like {0}", "%" + keyword + "%")
        }

        def goodsCatIds = goodsService.selectList(wrapper1).id
        def categoryList
        goodsCatIds = goodsCatIds.stream().distinct().collect(Collectors.toList())
        def wrapper2 = new EntityWrapper<WxMallCategoryDO>()
        wrapper2.in("id", goodsCatIds)
        wrapper2.where("level = {0}", "L2")
        if (goodsCatIds.size() != 0) {
            categoryList = categoryService.selectList(wrapper2)
        }

        def data = [:]
        data.put("goodsList", goodsList)
        data.put("filterCategoryList", categoryList)
        data.put("count", goodsList.size())
        HttpResponse.success(data)
    }

    /**
     * 商品页面推荐商品
     *
     * @return 商品页面推荐商品
     *   成功则
     *{
     *      errno: 0,
     *      errmsg: '成功',
     *      data:
     *{
     *              goodsList: xxx
     *}*}*   失败则 { errno: XXX, errmsg: XXX }
     */
    @GetMapping("/related")
    def related(Integer id) {
        if (id == null) {
            return HttpResponse.badArgument()
        }

        def goods = goodsService.selectById(id)
        if (goods == null) {
            return HttpResponse.badArgumentValue()
        }

        // 目前的商品推荐算法仅仅是推荐同类目的其他商品
        int cid = goods.getCategoryId()

        // 查找六个相关商品
        int related = 6
        def goodsList = goodsService.selectPage(new Page<WxMallGoodsDO>(0, related), new EntityWrapper<WxMallGoodsDO>()
                .where("category_id = {0}", cid)).getRecords()
        def data = [:]
        data.put("goodsList", goodsList)
        HttpResponse.success(data)
    }

    /**
     * 在售的商品总数
     *
     * @return 在售的商品总数
     *   成功则
     *{
     *      errno: 0,
     *      errmsg: '成功',
     *      data:
     *{
     *              goodsCount: xxx
     *}*}*   失败则 { errno: XXX, errmsg: XXX }
     */
    @GetMapping("count")
    def count() {
        def goodsCount = goodsService.selectCount(new EntityWrapper<WxMallGoodsDO>()
                .where("is_on_sale = true and deleted = false"))
        def data = [:]
        data.put("goodsCount", goodsCount)
        HttpResponse.success(data)
    }
}
