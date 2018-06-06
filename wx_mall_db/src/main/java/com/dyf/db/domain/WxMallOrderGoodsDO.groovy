package com.dyf.db.domain

import com.baomidou.mybatisplus.annotations.TableLogic
import com.baomidou.mybatisplus.annotations.TableName

import java.time.LocalDateTime

@TableName("wx_mall_order_goods")
class WxMallOrderGoodsDO {
    Integer id
    Integer orderId
    Integer goodsId
    String goodsName
    String goodsSn
    Integer productId
    Short number
    BigDecimal retailPrice
    String goodsSpecificationValues
    def goodsSpecificationIds
    String picUrl
    LocalDateTime addTime
    @TableLogic
    Boolean deleted
}
