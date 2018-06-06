package com.dyf.db.service

import com.baomidou.mybatisplus.service.impl.ServiceImpl
import com.dyf.db.domain.WxMallCartDO
import com.dyf.db.repository.WxMallCartRepository
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
@Primary
class WxMallCartService extends ServiceImpl<WxMallCartRepository, WxMallCartDO> {

}