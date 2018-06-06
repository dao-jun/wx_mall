package com.dyf.wxapi.web

import cn.binarywang.wx.miniapp.api.WxMaService
import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.dyf.core.utils.JacksonUtil
import com.dyf.core.utils.HttpResponse
import com.dyf.core.utils.bcrypt.BCryptPasswordEncoder
import com.dyf.db.domain.UserInfo
import com.dyf.db.domain.WxMallUserDO
import com.dyf.db.service.WxMallUserService
import com.dyf.wxapi.entiry.FullUserInfo
import com.dyf.wxapi.utils.IpUtils
import com.dyf.wxapi.utils.UserTokenManager
import me.chanjar.weixin.common.exception.WxErrorException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletRequest
import java.time.LocalDateTime

@RestController
@RequestMapping("/wx/auth")
class WxMallAuthController {
    @Autowired
    private WxMallUserService userService
    @Autowired
    private WxMaService wxMaService

    /**
     * 用户登录
     * @param body
     * @param request
     */
    @PostMapping("/login")
    def login(@RequestBody String body) {
        String username = JacksonUtil.parseString(body, "username")
        String password = JacksonUtil.parseString(body, "password")
        if (username == null || password == null) {
            return HttpResponse.badArgument()
        }
        def user = null
        def userList = userService.selectList(new EntityWrapper<WxMallUserDO>().where("username = {0}", username))
        if (userList.size() > 1) {
            return HttpResponse.fail502()
        } else if (userList.size() == 0) {
            return HttpResponse.badArgumentValue()
        } else {
            user = userList.get(0)
        }

        def encoder = new BCryptPasswordEncoder()
        if (!encoder.matches(password, user.password)) {
            return HttpResponse.fail(403, "帐号或密码不正确")
        }

        def userInfo = new UserInfo()
        userInfo.nickName = username
        userInfo.avatarUrl = user.avatar

        def userToken = UserTokenManager.generateToken(user.id)
        def result = new HashMap()
        result.put("token", userToken.token)
        result.put("tokenExpire", userToken.expireTime.toString())
        result.put("userInfo", userInfo)
        HttpResponse.success(result)
    }
    /**
     * 微信登录
     * @param body
     * @param request
     * @return
     */
    @PostMapping("/login_by_weixin")
    def loginByWx(@RequestBody String body, HttpServletRequest request) {
        def code = JacksonUtil.parseString(body, "code")
        def fullUserInfo = JacksonUtil.parseObject(body, "userInfo", FullUserInfo.class)
        if (code == null || fullUserInfo == null) {
            return HttpResponse.badArgument()
        }

        def userInfo = fullUserInfo.userInfo
        def sessionKey = null
        def openId = null
        try {
            def result = this.wxMaService.getUserService().getSessionInfo(code)
            sessionKey = result.getSessionKey()
            openId = result.getOpenid()
        } catch (WxErrorException e) {
            e.printStackTrace()
        }

        if (sessionKey == null || openId == null) {
            return HttpResponse.fail()
        }

        if (!wxMaService.getUserService().checkUserInfo(sessionKey, fullUserInfo.getRawData(), fullUserInfo.getSignature())) {
            return HttpResponse.fail()
        }

        def userDO = userService.selectOne(new EntityWrapper<WxMallUserDO>().where("weixin_openid = {0}", openId))
        if (userDO == null) {
            userDO = new WxMallUserDO()
            userDO.username = userInfo.nickName
            userDO.password = openId
            userDO.weixinOpenid = openId
            userDO.avatar = userInfo.avatarUrl
            userDO.nickname = userInfo.nickName
            userDO.gender = (userInfo.gender == 1 ? "男" : "女")
            userDO.userLevel = "普通用户"
            userDO.status = "可用"
            userDO.lastLoginTime = LocalDateTime.now()
            userDO.lastLoginIp = IpUtils.client(request)
            userService.insert(userDO)
        } else {
            userDO.lastLoginTime = LocalDateTime.now()
            userDO.lastLoginIp = IpUtils.client(request)
            userService.update(userDO, new EntityWrapper<WxMallUserDO>().where("id = {0}", userDO.id))
        }

        def userToken = UserTokenManager.generateToken(userDO.id)

        def result = new HashMap()
        result.put("token", userToken.token)
        result.put("tokenExpire", userToken.expireTime.toString())
        result.put("userInfo", userInfo)
        return HttpResponse.success(result)
    }
    /**
     * 用户注册
     *
     * @param body
     * @param request
     * @return
     */
    @PostMapping("/register")
    def register(@RequestBody String body, HttpServletRequest request) {
        def username = JacksonUtil.parseString(body, "username")
        def password = JacksonUtil.parseString(body, "password")
        def mobile = JacksonUtil.parseString(body, "mobile")
        def code = JacksonUtil.parseString(body, "code")
        if (username == null || password == null || mobile == null || code == null) {
            return HttpResponse.badArgument();
        }

        def userList = userService.selectList(new EntityWrapper<WxMallUserDO>().where("username = {0}", username))
        if (userList.size() > 0) {
            return HttpResponse.fail(403, "用户名已注册")
        }

        userList = userService.selectList(new EntityWrapper<WxMallUserDO>().where("username = {0}", username).and("deleted = {0}", false))

        if (userList.size() > 0) {
            return HttpResponse.fail(403, "用户名已经注册")
        }

        userList = userService.selectList(new EntityWrapper<WxMallUserDO>().where("mobile = {0}", mobile).and("deleted= {0}", false))
        if (userList.size() > 0) {
            return HttpResponse.fail(403, "手机号码已经注册！")
        }

        def user = new WxMallUserDO()
        def encode = new BCryptPasswordEncoder()
        def encodedPassword = encode.encode(password)
        user.username = username
        user.password = encodedPassword
        user.mobile = mobile
        user.weixinOpenid = ""
        user.avatar = "https://yanxuan.nosdn.127.net/80841d741d7fa3073e0ae27bf487339f.jpg?imageView&quality=90&thumbnail=64x64"
        user.nickname = username
        user.gender = "未知"
        user.userLevel = "普通用户"
        user.status = "可用"
        user.lastLoginTime = LocalDateTime.now()
        user.lastLoginIp = IpUtils.client(request)
        userService.insert(user)

        def userInfo = new UserInfo()
        userInfo.nickName = username
        userInfo.avatarUrl = user.avatar

        def userToken = UserTokenManager.generateToken(user.id)
        def result = new HashMap()
        result.put("token", userToken.token)
        result.put("tokenExpire", userToken.expireTime.toString())
        result.put("userInfo", userInfo)
        HttpResponse.success(result)
    }
    /**
     * 用户重置密码
     *
     * @param body
     * @param request
     * @return
     */
    @PostMapping("/reset")
    def reset(@RequestBody String body) {
        def password = JacksonUtil.parseString(body, "password")
        def mobile = JacksonUtil.parseString(body, "mobile")
        def code = JacksonUtil.parseString(body, "code")
        if (mobile == null || code == null || password == null) {
            return HttpResponse.badArgument()
        }

        def userList = userService.selectList(new EntityWrapper<WxMallUserDO>().where("mobile = {0}", mobile).and("deleted = {0}", false))
        def user = null
        if (userList.size() > 1) {
            return HttpResponse.serious()
        } else if (userList.size() == 0) {
            return HttpResponse.fail(403, "手机号未注册")
        } else {
            user = userList.get(0)
        }

        def encoder = new BCryptPasswordEncoder()
        def encodedPwd = encoder.encode(password)
        user.password = encodedPwd
        userService.update(user, new EntityWrapper<WxMallUserDO>().where("id = {0}", user.id))
        HttpResponse.success()
    }
}
