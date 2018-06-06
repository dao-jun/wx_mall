package com.dyf.db.service

import com.baomidou.mybatisplus.service.impl.ServiceImpl
import com.dyf.db.domain.WxMallRegionDO
import com.dyf.db.repository.WxMallRegionRepository
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
@Primary
class WxMallRegionService extends ServiceImpl<WxMallRegionRepository, WxMallRegionDO> {
}
