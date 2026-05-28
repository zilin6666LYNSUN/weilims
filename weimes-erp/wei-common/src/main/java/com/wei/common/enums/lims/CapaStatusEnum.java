package com.wei.common.enums.lims;

/**
 * CAPA状态枚举
 */
public enum CapaStatusEnum {
    DRAFT("草稿", "DRAFT"),
    IN_PROGRESS("执行中", "IN_PROGRESS"),
    COMPLETED("已完成", "COMPLETED"),
    CLOSED("已关闭", "CLOSED"),
    OVERDUE("逾期", "OVERDUE");

    private final String label;
    private final String code;

    CapaStatusEnum(String label, String code) {
        this.label = label;
        this.code = code;
    }

    public String getLabel() { return label; }
    public String getCode() { return code; }
}
