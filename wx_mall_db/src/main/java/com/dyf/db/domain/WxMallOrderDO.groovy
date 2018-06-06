package com.dyf.db.domain

import com.baomidou.mybatisplus.annotations.TableId
import com.baomidou.mybatisplus.annotations.TableLogic
import com.baomidou.mybatisplus.annotations.TableName

import java.time.LocalDateTime

@TableName("wx_mall_order")
class WxMallOrderDO {
    @TableId
    Integer id
    Integer userId
    String orderSn
    Short orderStatus
    String consignee
    String mobile
    String address
    BigDecimal goodsPrice
    BigDecimal freightPrice
    BigDecimal couponPrice
    BigDecimal integralPrice
    BigDecimal orderPrice
    BigDecimal actualPrice
    String payId
    Short payStatus
    LocalDateTime payTime
    String shipSn
    String shipChannel
    LocalDateTime shipStartTime
    LocalDateTime shipEndTime
    LocalDateTime confirmTime
    LocalDateTime endTime
    LocalDateTime addTime
    @TableLogic
    Boolean deleted
}
