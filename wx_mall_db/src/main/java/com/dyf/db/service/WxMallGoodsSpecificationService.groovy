package com.dyf.db.service

import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.baomidou.mybatisplus.service.impl.ServiceImpl
import com.dyf.db.domain.WxMallGoodsSpecificationDO
import com.dyf.db.repository.WxMallGoodsSpecificationRepository
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Primary
@Transactional
class WxMallGoodsSpecificationService extends ServiceImpl<WxMallGoodsSpecificationRepository, WxMallGoodsSpecificationDO> {

    def getSpecificationVoList(Integer id) {
        def goodsSpecificationList = this.baseMapper.selectList(new EntityWrapper<WxMallGoodsSpecificationDO>()
                .where("goods_id = {0} and deleted = false", id))

        def map = [:]
        def specificationVoList = []

        for (WxMallGoodsSpecificationDO goodsSpecification : goodsSpecificationList) {
            def specification = goodsSpecification.specification
            VO goodsSpecificationVo = map.get(specification)
            if (goodsSpecificationVo == null) {
                goodsSpecificationVo = new VO()
                goodsSpecificationVo.name = specification
                def valueList = []
                valueList.add(goodsSpecification)
                goodsSpecificationVo.valueList = valueList
                map.put(specification, goodsSpecificationVo)
                specificationVoList.add(goodsSpecificationVo)
            } else {
                def valueList = goodsSpecificationVo.valueList
                valueList.add(goodsSpecification)
            }
        }

        return specificationVoList
    }
}

class VO {
    String name
    List<WxMallGoodsSpecificationDO> valueList
}
