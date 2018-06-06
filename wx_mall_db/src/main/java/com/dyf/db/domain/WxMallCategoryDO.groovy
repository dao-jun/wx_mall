package com.dyf.db.domain

import com.baomidou.mybatisplus.annotations.TableLogic
import com.baomidou.mybatisplus.annotations.TableName

import java.time.LocalDateTime

@TableName("wx_mall_category")
class WxMallCategoryDO {

    Integer id
    String name
    String keywords
    String frontDesc
    Integer parentId
    Byte sortOrder
    Byte showIndex
    Boolean isShow
    String bannerUrl
    String iconUrl
    String imgUrl
    String wapBannerUrl
    String level
    Integer type
    String frontName
    LocalDateTime addTime
    @TableLogic
    Boolean deleted
}
