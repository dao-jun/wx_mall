package com.dyf.db.domain

import com.baomidou.mybatisplus.annotations.TableLogic
import com.baomidou.mybatisplus.annotations.TableName

import java.time.LocalDateTime

@TableName("wx_mall_topic")
class WxMallTopicDO {
    Integer id
    String title
    String avatar
    String itemPicUrl
    String subtitle
    BigDecimal priceInfo
    String readCount
    String scenePicUrl
    Integer sortOrder
    Boolean isShow
    LocalDateTime addTime
    @TableLogic
    Boolean deleted
    String content
}
