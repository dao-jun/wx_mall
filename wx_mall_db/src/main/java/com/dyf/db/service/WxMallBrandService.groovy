package com.dyf.db.service

import com.baomidou.mybatisplus.service.impl.ServiceImpl
import com.dyf.db.domain.WxMallBrandDO
import com.dyf.db.repository.WxMallBrandRepository
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
@Primary
class WxMallBrandService extends ServiceImpl<WxMallBrandRepository, WxMallBrandDO> {
}
