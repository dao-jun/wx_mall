package com.dyf.db.domain

import com.baomidou.mybatisplus.annotations.TableId
import com.baomidou.mybatisplus.annotations.TableLogic
import com.baomidou.mybatisplus.annotations.TableName

import java.time.LocalDateTime

@TableName("wx_mall_keyword")
class WxMallKeywordDO {
    @TableId
    Integer id
    String keyword
    String url
    Boolean isHot
    Boolean isDefault
    Boolean isShow
    Integer sortOrder
    LocalDateTime addTime
    @TableLogic
    Boolean deleted
}
