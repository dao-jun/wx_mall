package com.dyf.db.repository

import com.baomidou.mybatisplus.mapper.BaseMapper
import com.dyf.db.domain.WxMallProductDO
import org.apache.ibatis.annotations.Mapper
import org.springframework.stereotype.Repository

@Repository
interface WxMallProductRepository extends BaseMapper<WxMallProductDO> {
}
