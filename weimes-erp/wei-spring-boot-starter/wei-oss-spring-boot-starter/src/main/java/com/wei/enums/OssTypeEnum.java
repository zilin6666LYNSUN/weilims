package com.wei.enums;

import lombok.Getter;

/**
 * @author wei
 * @date 2025/1/16 22:36
 */
@Getter
public enum OssTypeEnum {
    Default("minIOService"), MinIO("minIOService"), AliYun("aliYunService"),HuaWeiObs("huaWeiObsService");

    private final String type;

    OssTypeEnum(String type) {
        this.type = type;
    }

}
