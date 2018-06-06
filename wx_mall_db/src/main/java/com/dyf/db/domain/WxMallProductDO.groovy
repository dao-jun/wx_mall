package com.dyf.db.domain

import com.baomidou.mybatisplus.annotations.TableField
import com.baomidou.mybatisplus.annotations.TableLogic
import com.baomidou.mybatisplus.annotations.TableName

import java.time.LocalDateTime

@TableName("wx_mall_product")
class WxMallProductDO {
    Integer id
    Integer goodsId
    def goodsSpecificationIds
    Integer goodsNumber
    BigDecimal retailPrice
    String url
    LocalDateTime addTime
    @TableField("goods_specification_ids")
    String ids
    @TableLogic
    Boolean deleted
}
