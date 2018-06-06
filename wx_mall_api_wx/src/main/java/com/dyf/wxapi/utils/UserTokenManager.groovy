package com.dyf.wxapi.utils

import com.dyf.core.utils.CharUtil
import com.dyf.wxapi.entiry.UserToken

import java.time.LocalDateTime

class UserTokenManager {
    private static def tokenMap = new HashMap<String, UserToken>()
    private static def idMap = new HashMap<Integer, UserToken>()

    static Integer getUserId(String token) {


        UserToken userToken = tokenMap.get(token);
        if (userToken == null) {
            return null
        }

        if (userToken.getExpireTime().isBefore(LocalDateTime.now())) {
            tokenMap.remove(token)
            idMap.remove(userToken.getUserId())
            return null
        }

        return userToken.getUserId()
    }


    static UserToken generateToken(Integer id) {
        def userToken = null

        def token = CharUtil.getRandomString(32)
        while (tokenMap.containsKey(token)) {
            token = CharUtil.getRandomString(32)
        }

        def update = LocalDateTime.now()
        def expire = update.plusDays(1)

        userToken = new UserToken()
        userToken.setToken(token)
        userToken.setUpdateTime(update)
        userToken.setExpireTime(expire)
        userToken.setUserId(id)
        tokenMap.put(token, userToken)
        idMap.put(id, userToken)

        return userToken
    }
}
