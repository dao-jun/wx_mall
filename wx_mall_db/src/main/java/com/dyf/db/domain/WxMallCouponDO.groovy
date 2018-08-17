package com.dyf.db.domain

import com.baomidou.mybatisplus.annotations.TableId
import com.baomidou.mybatisplus.annotations.TableLogic
import com.baomidou.mybatisplus.annotations.TableName

import java.time.LocalDateTime

@TableName("wx_mall_coupon")
class WxMallCouponDO {
    @TableId
    Integer id
    String name
    BigDecimal typeMoney
    Byte sendType
    BigDecimal minAmount
    BigDecimal maxAmount
    BigDecimal minGoodsAmount
    LocalDateTime sendStart
    LocalDateTime sendEnd
    LocalDateTime useStart
    LocalDateTime useEnd
    LocalDateTime addTime
    @TableLogic
    Boolean deleted
}
