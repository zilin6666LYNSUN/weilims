package com.wei.common.enums.lims;

/**
 * 校准状态枚举
 */
public enum CalibrationStatusEnum {
    QUALIFIED("合格", "QUALIFIED"),
    UNQUALIFIED("不合格", "UNQUALIFIED"),
    PENDING("待校准", "PENDING"),
    OVERDUE("已过期", "OVERDUE");

    private final String label;
    private final String code;

    CalibrationStatusEnum(String label, String code) {
        this.label = label;
        this.code = code;
    }

    public String getLabel() { return label; }
    public String getCode() { return code; }
}
