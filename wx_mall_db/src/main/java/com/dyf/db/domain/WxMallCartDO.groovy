package com.dyf.db.domain

import com.baomidou.mybatisplus.annotations.TableLogic
import com.baomidou.mybatisplus.annotations.TableName

import java.time.LocalDateTime

@TableName("wx_mall_cart")
class WxMallCartDO {
    Integer id
    Integer userId
    Integer goodsId
    String goodsSn
    Integer productId
    String goodsName
    BigDecimal retailPrice
    Short number
    String goodsSpecificationValues
    def goodsSpecificationIds
    Boolean checked
    String picUrl
    LocalDateTime addTime
    @TableLogic
    Boolean deleted
}
