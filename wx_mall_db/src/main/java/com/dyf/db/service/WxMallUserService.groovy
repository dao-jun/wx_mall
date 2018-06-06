package com.dyf.db.service

import com.baomidou.mybatisplus.service.impl.ServiceImpl
import com.dyf.db.domain.UserInfo
import com.dyf.db.domain.WxMallUserDO
import com.dyf.db.repository.WxMallUserRepository
import org.apache.catalina.User
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.Assert

@Service
@Primary
@Transactional
class WxMallUserService extends ServiceImpl<WxMallUserRepository, WxMallUserDO> {


    UserInfo getInfo(Integer userId) {
        WxMallUserDO userDO = this.baseMapper.selectById(userId)
        Assert.state(userDO != null, "用户不存在")
        def userInfo = new UserInfo()
        userInfo.nickName = userDO.nickname
        userInfo.avatarUrl = userDO.avatar
        userInfo
    }
}
