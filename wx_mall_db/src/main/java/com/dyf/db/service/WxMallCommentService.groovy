package com.dyf.db.service

import com.baomidou.mybatisplus.service.impl.ServiceImpl
import com.dyf.db.domain.WxMallCommentDO
import com.dyf.db.repository.WxMallCommentRepository
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Primary
@Service
class WxMallCommentService extends ServiceImpl<WxMallCommentRepository,WxMallCommentDO>{

}