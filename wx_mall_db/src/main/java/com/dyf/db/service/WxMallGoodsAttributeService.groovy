package com.dyf.db.service

import com.baomidou.mybatisplus.service.impl.ServiceImpl
import com.dyf.db.domain.WxMallGoodsAttributeDO
import com.dyf.db.repository.WxMallGoodsAttributeRepository
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Primary
@Transactional
class WxMallGoodsAttributeService extends ServiceImpl<WxMallGoodsAttributeRepository, WxMallGoodsAttributeDO> {
}
