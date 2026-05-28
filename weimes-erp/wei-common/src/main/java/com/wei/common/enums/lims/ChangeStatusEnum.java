package com.wei.common.enums.lims;

/**
 * 变更控制状态枚举
 */
public enum ChangeStatusEnum {
    APPLYING("申请中", "APPLYING"),
    ASSESSING("评估中", "ASSESSING"),
    APPROVING("审批中", "APPROVING"),
    EXECUTING("执行中", "EXECUTING"),
    EFFECTIVENESS_REVIEW("效果确认", "EFFECTIVENESS_REVIEW"),
    CLOSED("已关闭", "CLOSED");

    private final String label;
    private final String code;

    ChangeStatusEnum(String label, String code) {
        this.label = label;
        this.code = code;
    }

    public String getLabel() { return label; }
    public String getCode() { return code; }
}
