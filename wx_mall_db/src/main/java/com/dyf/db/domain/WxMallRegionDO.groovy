package com.dyf.db.domain

import com.baomidou.mybatisplus.annotations.TableId
import com.baomidou.mybatisplus.annotations.TableName

@TableName("wx_mall_region")
class WxMallRegionDO {
    @TableId
    Integer id
    Integer pid
    String name
    Byte type
    Integer code
}
