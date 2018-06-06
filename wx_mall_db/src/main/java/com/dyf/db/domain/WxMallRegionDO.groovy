package com.dyf.db.domain

import com.baomidou.mybatisplus.annotations.TableName

@TableName("wx_mall_region")
class WxMallRegionDO {
    Integer id
    Integer pid
    String name
    Byte type
    Integer code
}
