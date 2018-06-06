package com.dyf.wxapi.entiry

import java.time.LocalDateTime

class UserToken {
    Integer userId
    String token
    LocalDateTime expireTime
    LocalDateTime updateTime
}
