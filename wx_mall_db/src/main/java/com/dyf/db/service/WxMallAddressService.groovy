package com.dyf.db.service

import com.baomidou.mybatisplus.service.impl.ServiceImpl
import com.dyf.db.domain.WxMallAddressDO
import com.dyf.db.repository.WxMallAddressRepository
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
@Primary
class WxMallAddressService extends ServiceImpl<WxMallAddressRepository, WxMallAddressDO> {
}
