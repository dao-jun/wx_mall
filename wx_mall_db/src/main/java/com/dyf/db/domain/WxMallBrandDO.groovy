package com.dyf.db.domain

import com.baomidou.mybatisplus.annotations.TableId
import com.baomidou.mybatisplus.annotations.TableLogic
import com.baomidou.mybatisplus.annotations.TableName

import java.time.LocalDateTime

@TableName("wx_mall_brand")
class WxMallBrandDO {
    @TableId
    Integer id
    String name
    String listPicUrl
    String simpleDesc
    String picUrl
    Byte sortOrder
    Boolean isShow
    BigDecimal floorPrice
    String appListPicUrl
    Boolean isNew
    String newPicUrl
    Byte newSortOrder
    LocalDateTime addTime
    @TableLogic
    Boolean deleted
}
