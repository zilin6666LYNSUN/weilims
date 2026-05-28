package com.wei.common.enums.lims;

/**
 * 偏差状态枚举
 */
public enum DeviationStatusEnum {
    DRAFT("草稿", "DRAFT"),
    INVESTIGATING("调查中", "INVESTIGATING"),
    CAPA_EXECUTING("CAPA执行中", "CAPA_EXECUTING"),
    EFFECTIVENESS_REVIEW("效果确认中", "EFFECTIVENESS_REVIEW"),
    CLOSED("已关闭", "CLOSED");

    private final String label;
    private final String code;

    DeviationStatusEnum(String label, String code) {
        this.label = label;
        this.code = code;
    }

    public String getLabel() { return label; }
    public String getCode() { return code; }
}
