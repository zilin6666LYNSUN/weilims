package com.wei.common.enums.lims;

/**
 * 检验订单状态枚举
 */
public enum TestOrderStatusEnum {
    DRAFT("草稿", "DRAFT"),
    SUBMITTED("已提交", "SUBMITTED"),
    TESTING("检测中", "TESTING"),
    TESTED("已检测", "TESTED"),
    REVIEWING("复核中", "REVIEWING"),
    REVIEWED("已复核", "REVIEWED"),
    APPROVING("审批中", "APPROVING"),
    APPROVED("已批准", "APPROVED"),
    REPORTED("已报告", "REPORTED");

    private final String label;
    private final String code;

    TestOrderStatusEnum(String label, String code) {
        this.label = label;
        this.code = code;
    }

    public String getLabel() { return label; }
    public String getCode() { return code; }
}
