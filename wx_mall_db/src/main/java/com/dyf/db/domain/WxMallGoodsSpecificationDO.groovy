package com.dyf.db.domain

import com.baomidou.mybatisplus.annotations.TableLogic
import com.baomidou.mybatisplus.annotations.TableName

import java.time.LocalDateTime

@TableName("wx_mall_goods_specification")
class WxMallGoodsSpecificationDO {
    Integer id
    Integer goodsId
    String value
    String picUrl
    String specification
    LocalDateTime addTime
    @TableLogic
    Boolean deleted

}
