package com.dyf.db.domain

import com.baomidou.mybatisplus.annotations.TableField
import com.baomidou.mybatisplus.annotations.TableId
import com.baomidou.mybatisplus.annotations.TableLogic
import com.baomidou.mybatisplus.annotations.TableName

import java.time.LocalDateTime

@TableName("wx_mall_address")
class WxMallAddressDO {
    @TableId
    Integer id
    String name
    Integer userId
    Integer provinceId
    Integer cityId
    Integer areaId
    String address
    String mobile
    @TableField("is_default")
    boolean defaulted
    LocalDateTime AddTime
    @TableLogic
    Boolean deleted

}
