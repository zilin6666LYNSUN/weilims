package com.wei.common.enums.lims;

/**
 * 试剂状态枚举
 */
public enum ReagentStatusEnum {
    IN_STOCK("在库", "IN_STOCK"),
    IN_USE("使用中", "IN_USE"),
    EXPIRED("已过期", "EXPIRED"),
    CONSUMED("已消耗", "CONSUMED"),
    DISPOSED("已处置", "DISPOSED");

    private final String label;
    private final String code;

    ReagentStatusEnum(String label, String code) {
        this.label = label;
        this.code = code;
    }

    public String getLabel() { return label; }
    public String getCode() { return code; }
}
