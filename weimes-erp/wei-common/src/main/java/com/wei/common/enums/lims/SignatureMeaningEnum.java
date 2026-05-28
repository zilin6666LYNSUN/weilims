package com.wei.common.enums.lims;

/**
 * 签名含义枚举(21 CFR Part 11)
 */
public enum SignatureMeaningEnum {
    DRAFTED("起草", "DRAFTED"),
    REVIEWED("复核", "REVIEWED"),
    APPROVED("批准", "APPROVED"),
    REJECTED("拒绝", "REJECTED"),
    VERIFIED("验证", "VERIFIED"),
    CANCELLED("作废", "CANCELLED");

    private final String label;
    private final String code;

    SignatureMeaningEnum(String label, String code) {
        this.label = label;
        this.code = code;
    }

    public String getLabel() { return label; }
    public String getCode() { return code; }
}
