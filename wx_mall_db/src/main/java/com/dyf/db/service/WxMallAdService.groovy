package com.dyf.db.service

import com.baomidou.mybatisplus.service.impl.ServiceImpl
import com.dyf.db.domain.WxMallAdDO
import com.dyf.db.repository.WxMallAdRepository
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
@Primary
class WxMallAdService extends ServiceImpl<WxMallAdRepository, WxMallAdDO> {

}