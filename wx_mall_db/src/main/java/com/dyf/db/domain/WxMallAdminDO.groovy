package com.dyf.db.domain

import com.baomidou.mybatisplus.annotations.TableId
import com.baomidou.mybatisplus.annotations.TableLogic
import com.baomidou.mybatisplus.annotations.TableName

import java.time.LocalDateTime

@TableName("wx_mall_admin")
class WxMallAdminDO {
    @TableId
    Integer id
    String username
    String password
    String lastLoginIp
    LocalDateTime lastLoginTime
    LocalDateTime updateTime
    String avatar
    LocalDateTime addTime
    @TableLogic()
    Boolean deleted
}
