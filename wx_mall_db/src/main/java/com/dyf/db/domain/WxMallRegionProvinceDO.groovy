package com.dyf.db.domain

import com.baomidou.mybatisplus.annotations.TableId
import com.baomidou.mybatisplus.annotations.TableName

@TableName("wx_mall_region_province")
class WxMallRegionProvinceDO {
    @TableId
    Integer code
    String name

}
