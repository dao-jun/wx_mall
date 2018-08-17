package com.dyf.db.domain

import com.baomidou.mybatisplus.annotations.TableId
import com.baomidou.mybatisplus.annotations.TableLogic
import com.baomidou.mybatisplus.annotations.TableName

import java.time.LocalDate
import java.time.LocalDateTime

@TableName("wx_mall_user")
class WxMallUserDO {
    @TableId
    Integer id
    String username
    String password
    String gender
    LocalDate birthday
    LocalDateTime lastLoginTime
    String lastLoginIp
    String userLevel
    String nickname
    String mobile
    String registerIp
    String avatar
    String weixinOpenid
    String status
    LocalDateTime addTime
    @TableLogic
    Boolean deleted
}
