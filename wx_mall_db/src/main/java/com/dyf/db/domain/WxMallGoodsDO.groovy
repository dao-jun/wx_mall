package com.dyf.db.domain

import com.baomidou.mybatisplus.annotations.TableId
import com.baomidou.mybatisplus.annotations.TableLogic
import com.baomidou.mybatisplus.annotations.TableName

import java.time.LocalDateTime

/**
 * create by dyf
 * 商品
 */
@TableName("wx_mall_goods")
class WxMallGoodsDO {
    @TableId
    Integer id
    String goodsSn
    String name
    Integer categoryId
    Integer brandId
    String gallery
    String keywords
    String goodsBrief
    Boolean isOnSale
    Short sortOrder
    BigDecimal counterPrice
    Boolean isNew
    String primaryPicUrl
    String listPicUrl
    Boolean isHot
    String goodsUnit
    BigDecimal retailPrice
    LocalDateTime addTime
    @TableLogic
    Boolean deleted
    String goodsDesc
}
