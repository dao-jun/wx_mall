package com.dyf.db.domain

import com.baomidou.mybatisplus.annotations.TableField
import com.baomidou.mybatisplus.annotations.TableId
import com.baomidou.mybatisplus.annotations.TableLogic
import com.baomidou.mybatisplus.annotations.TableName

import java.time.LocalDateTime

@TableName("wx_mall_collect")
class WxMallCollectDO {
    @TableId
    Integer id
    Integer userId
    Integer valueId
    @TableField("is_attention")
    Boolean attention
    Integer typeId
    LocalDateTime addTime
    @TableLogic
    Boolean deleted

}
