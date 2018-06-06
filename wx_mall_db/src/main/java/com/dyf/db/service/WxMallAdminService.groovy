package com.dyf.db.service

import com.baomidou.mybatisplus.service.impl.ServiceImpl
import com.dyf.db.domain.WxMallAdminDO
import com.dyf.db.repository.WxMallAdminRepository
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
@Primary
class WxMallAdminService extends ServiceImpl<WxMallAdminRepository, WxMallAdminDO> {
}
