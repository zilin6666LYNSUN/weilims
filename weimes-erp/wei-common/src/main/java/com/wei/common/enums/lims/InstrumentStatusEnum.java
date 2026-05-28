package com.wei.common.enums.lims;

/**
 * 仪器状态枚举
 */
public enum InstrumentStatusEnum {
    NORMAL("正常", "NORMAL"),
    CALIBRATION_DUE("待校准", "CALIBRATION_DUE"),
    MAINTENANCE("维护中", "MAINTENANCE"),
    OUT_OF_SERVICE("停用", "OUT_OF_SERVICE"),
    SCRAPPED("报废", "SCRAPPED");

    private final String label;
    private final String code;

    InstrumentStatusEnum(String label, String code) {
        this.label = label;
        this.code = code;
    }

    public String getLabel() { return label; }
    public String getCode() { return code; }
}
