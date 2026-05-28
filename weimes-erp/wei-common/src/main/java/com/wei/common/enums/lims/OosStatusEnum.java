package com.wei.common.enums.lims;

/**
 * OOS/OOT状态枚举
 */
public enum OosStatusEnum {
    INVESTIGATING("调查中", "INVESTIGATING"),
    PHASE_I("Phase I", "PHASE_I"),
    PHASE_II("Phase II", "PHASE_II"),
    CAPA_IN_PROGRESS("CAPA中", "CAPA_IN_PROGRESS"),
    CLOSED("已关闭", "CLOSED");

    private final String label;
    private final String code;

    OosStatusEnum(String label, String code) {
        this.label = label;
        this.code = code;
    }

    public String getLabel() { return label; }
    public String getCode() { return code; }
}
