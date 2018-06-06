package com.dyf.db.repository

import com.baomidou.mybatisplus.mapper.BaseMapper
import com.dyf.db.domain.WxMallGoodsAttributeDO
import org.apache.ibatis.annotations.Mapper
import org.springframework.stereotype.Repository

@Repository
interface WxMallGoodsAttributeRepository extends BaseMapper<WxMallGoodsAttributeDO> {
}
