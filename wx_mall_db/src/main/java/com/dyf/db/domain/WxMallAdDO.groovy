package com.dyf.db.domain

import com.baomidou.mybatisplus.annotations.TableId
import com.baomidou.mybatisplus.annotations.TableLogic
import com.baomidou.mybatisplus.annotations.TableName

import java.time.LocalDateTime

@TableName("wx_mall_ad")
class WxMallAdDO {
    @TableId
    Integer id
    Integer position
    String name
    String link
    String url
    String content
    LocalDateTime startTime
    LocalDateTime endTime
    Boolean enabled
    LocalDateTime addTime
    @TableLogic
    Boolean deleted

}
