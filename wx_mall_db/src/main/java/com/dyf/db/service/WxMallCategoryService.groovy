package com.dyf.db.service

import com.baomidou.mybatisplus.service.impl.ServiceImpl
import com.dyf.db.domain.WxMallCategoryDO
import com.dyf.db.repository.WxMallCategoryRepository
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
@Primary
class WxMallCategoryService extends ServiceImpl<WxMallCategoryRepository, WxMallCategoryDO> {
}
