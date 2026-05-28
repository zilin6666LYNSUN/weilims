package com.wei.lims.test.domain;

import java.math.BigDecimal;

/**
 * 质量标准行对象 lims_specification_line
 */
public class LimsSpecificationLine {
    private static final long serialVersionUID = 1L;

    private Long lineId;
    private Long specificationId;
    private Long testItemId;
    private String testItemName;
    private Long methodId;
    private String specificationValue;
    private BigDecimal lowerLimit;
    private BigDecimal upperLimit;
    private String unit;
    private Integer sequence;
    private Integer isMandatory;
    private String roundingRule;

    public Long getLineId() { return lineId; }
    public void setLineId(Long lineId) { this.lineId = lineId; }
    public Long getSpecificationId() { return specificationId; }
    public void setSpecificationId(Long specificationId) { this.specificationId = specificationId; }
    public Long getTestItemId() { return testItemId; }
    public void setTestItemId(Long testItemId) { this.testItemId = testItemId; }
    public String getTestItemName() { return testItemName; }
    public void setTestItemName(String testItemName) { this.testItemName = testItemName; }
    public Long getMethodId() { return methodId; }
    public void setMethodId(Long methodId) { this.methodId = methodId; }
    public String getSpecificationValue() { return specificationValue; }
    public void setSpecificationValue(String specificationValue) { this.specificationValue = specificationValue; }
    public BigDecimal getLowerLimit() { return lowerLimit; }
    public void setLowerLimit(BigDecimal lowerLimit) { this.lowerLimit = lowerLimit; }
    public BigDecimal getUpperLimit() { return upperLimit; }
    public void setUpperLimit(BigDecimal upperLimit) { this.upperLimit = upperLimit; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Integer getSequence() { return sequence; }
    public void setSequence(Integer sequence) { this.sequence = sequence; }
    public Integer getIsMandatory() { return isMandatory; }
    public void setIsMandatory(Integer isMandatory) { this.isMandatory = isMandatory; }
    public String getRoundingRule() { return roundingRule; }
    public void setRoundingRule(String roundingRule) { this.roundingRule = roundingRule; }
}
