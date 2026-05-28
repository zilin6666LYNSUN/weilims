package com.wei.common.enums.system;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 状态枚举
 *
 * @author wei
 */
@Getter
public enum StatusEnum  {

    ENABLE("0", "启用"),
    DISABLE("1", "停用");

    @EnumValue
    @JsonValue
    private final String code;
    private final String name;

    StatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
