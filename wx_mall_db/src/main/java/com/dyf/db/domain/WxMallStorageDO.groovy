package com.dyf.db.domain

import com.baomidou.mybatisplus.annotations.TableLogic
import com.baomidou.mybatisplus.annotations.TableName

import java.time.LocalDateTime

@TableName("wx_mall_storage")
class WxMallStorageDO {
    Integer id
    String key
    String name
    String type
    Integer size
    LocalDateTime modified
    String url
    LocalDateTime addTime
    @TableLogic
    Boolean deleted

}
