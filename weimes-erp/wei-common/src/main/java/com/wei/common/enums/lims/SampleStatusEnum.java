package com.wei.common.enums.lims;

/**
 * 样品状态枚举
 */
public enum SampleStatusEnum {
    PENDING_RECEIVE("待接收", "PENDING_RECEIVE"),
    RECEIVED("已接收", "RECEIVED"),
    TESTING("检测中", "TESTING"),
    TESTED("检测完成", "TESTED"),
    RETAINED("留样", "RETAINED"),
    DISPOSED("已处置", "DISPOSED");

    private final String label;
    private final String code;

    SampleStatusEnum(String label, String code) {
        this.label = label;
        this.code = code;
    }

    public String getLabel() { return label; }
    public String getCode() { return code; }
}
