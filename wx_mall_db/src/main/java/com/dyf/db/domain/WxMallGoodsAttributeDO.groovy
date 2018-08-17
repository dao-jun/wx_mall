package com.dyf.db.domain

import com.baomidou.mybatisplus.annotations.TableId
import com.baomidou.mybatisplus.annotations.TableLogic
import com.baomidou.mybatisplus.annotations.TableName

import java.time.LocalDateTime

@TableName("wx_mall_goods_attribute")
class WxMallGoodsAttributeDO {
    @TableId
    Integer id
    Integer goodsId
    String value
    String attribute
    LocalDateTime addTime
    @TableLogic
    Boolean deleted

}
