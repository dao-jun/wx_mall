package com.dyf.apiadmin.utils

import com.dyf.core.utils.CharUtil

import java.time.LocalDateTime

class AdminTokenManager {
    private static def tokenMap = new HashMap()
    private static def idMap = new HashMap()

    static Integer getUserId(String token) {
        AdminToken userToken = tokenMap.get(token)
        if (userToken == null) {
            return null
        }

        if (userToken.expireTime.isBefore(LocalDateTime.now())) {
            tokenMap.remove(token)
            idMap.remove(userToken.userId)
            return null
        }
        userToken.userId
    }

    static def generateToken(Integer id) {
        def userToken

        def token = CharUtil.getRandomString(32)
        while (tokenMap.containsKey(token)) {
            token = CharUtil.getRandomString(32)
        }

        def update = LocalDateTime.now()
        def expire = update.plusDays(1)

        userToken = new AdminToken()
        userToken.token = token
        userToken.updateTime = update
        userToken.expireTime = expire
        userToken.userId = id
        tokenMap.put(token, userToken)
        idMap.put(id, userToken)

        userToken
    }
}
