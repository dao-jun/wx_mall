package com.dyf.apiadmin.utils

import java.time.LocalDateTime

class AdminToken {
    Integer userId
    String token
    LocalDateTime expireTime
    LocalDateTime updateTime
}
