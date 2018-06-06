package com.dyf.wxapi.entiry;

import com.dyf.db.domain.UserInfo;
import lombok.Data;

@Data
public class FullUserInfo {
    private String encryptedData;
    private String errMsg;
    private String iv;
    private String rawData;
    private String signature;
    private UserInfo userInfo;
}
