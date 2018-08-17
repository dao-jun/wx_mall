package com.dyf.db.domain

import com.baomidou.mybatisplus.annotations.TableId
import com.baomidou.mybatisplus.annotations.TableLogic
import com.baomidou.mybatisplus.annotations.TableName

import java.time.LocalDateTime

/**
 * create by dyf
 * 浏览足迹
 */
@TableName("wx_mall_footprint")
class WxMallFootPrintDO {
    @TableId
    Integer id
    Integer userId
    Integer goodsId
    LocalDateTime addTime
    @TableLogic
    Boolean deleted
}
