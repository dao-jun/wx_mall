package com.dyf.db.domain

import com.baomidou.mybatisplus.annotations.TableId
import com.baomidou.mybatisplus.annotations.TableName

@TableName("wx_mall_region_street")
class WxMallRegionStreetDO {
    @TableId
    Integer code
    String name
    Integer areaCode
    Integer provinceCode
    Integer cityCode
}
