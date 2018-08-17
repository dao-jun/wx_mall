package com.dyf.db.domain

import com.baomidou.mybatisplus.annotations.TableId
import com.baomidou.mybatisplus.annotations.TableLogic
import com.baomidou.mybatisplus.annotations.TableName

import java.time.LocalDateTime

@TableName("wx_mall_comment")
class WxMallCommentDO {
    @TableId
    Integer id
    Byte typeId
    Integer valueId
    String content
    Integer userId
    Boolean hasPicture
    def picUrls
    Short star
    LocalDateTime addTime
    @TableLogic
    Boolean deleted

}
