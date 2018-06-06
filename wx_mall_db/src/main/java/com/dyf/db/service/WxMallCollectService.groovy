package com.dyf.db.service

import com.baomidou.mybatisplus.service.impl.ServiceImpl
import com.dyf.db.domain.WxMallCollectDO
import com.dyf.db.repository.WxMallCollectRepository
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
@Primary
class WxMallCollectService extends ServiceImpl<WxMallCollectRepository, WxMallCollectDO> {
}
