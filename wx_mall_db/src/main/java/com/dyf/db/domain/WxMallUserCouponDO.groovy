package com.dyf.db.domain

import com.baomidou.mybatisplus.annotations.TableId
import com.baomidou.mybatisplus.annotations.TableLogic
import com.baomidou.mybatisplus.annotations.TableName

import java.time.LocalDateTime

@TableName("wx_mall_user_coupon")
class WxMallUserCouponDO {
    @TableId
    Integer id
    Integer couponId
    Integer userId
    Integer orderId
    LocalDateTime usedTime
    LocalDateTime addTime
    @TableLogic
    Boolean deleted

}
