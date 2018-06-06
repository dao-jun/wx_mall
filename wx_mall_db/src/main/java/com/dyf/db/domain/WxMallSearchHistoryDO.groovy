package com.dyf.db.domain

import com.baomidou.mybatisplus.annotations.TableId
import com.baomidou.mybatisplus.annotations.TableLogic
import com.baomidou.mybatisplus.annotations.TableName

import java.time.LocalDateTime

@TableName("wx_mall_search_history")
class WxMallSearchHistoryDO {
    @TableId
    Integer id
    Integer userId
    String keyword
    String from
    LocalDateTime addTime
    @TableLogic
    Boolean deleted

}
