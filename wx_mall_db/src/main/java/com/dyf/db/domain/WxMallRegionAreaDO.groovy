package com.dyf.db.domain

import com.baomidou.mybatisplus.annotations.TableId
import com.baomidou.mybatisplus.annotations.TableName

@TableName("wx_mall_region_area")
class WxMallRegionAreaDO {

    @TableId
    Integer code
    String name
    Integer cityCode
    Integer provinceCode

}
