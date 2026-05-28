package com.wei.common.enums.lims;

/**
 * 伦理审查结果枚举
 */
public enum EthicsReviewResultEnum {
    PENDING("待审查", "PENDING"),
    APPROVED("已批准", "APPROVED"),
    APPROVED_WITH_MODIFICATIONS("修改后批准", "APPROVED_WITH_MODIFICATIONS"),
    NOT_APPROVED("不批准", "NOT_APPROVED"),
    SUSPENDED("暂停", "SUSPENDED");

    private final String label;
    private final String code;

    EthicsReviewResultEnum(String label, String code) {
        this.label = label;
        this.code = code;
    }

    public String getLabel() { return label; }
    public String getCode() { return code; }
}
