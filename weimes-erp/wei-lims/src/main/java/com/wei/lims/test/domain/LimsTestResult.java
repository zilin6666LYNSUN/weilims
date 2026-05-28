package com.wei.lims.test.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wei.common.annotation.Excel;
import com.wei.common.core.domain.BaseEntity;

/**
 * 检验结果对象 lims_test_result
 */
public class LimsTestResult extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long resultId;
    private Long testOrderId;
    private Long specLineId;

    @Excel(name = "检测项名称")
    private String testItemName;

    private Long methodId;

    @Excel(name = "检测项类型")
    private String testItemType;

    @Excel(name = "原始值")
    private String originalValue;

    private String instrumentValue;

    private BigDecimal calculatedValue;

    @Excel(name = "报告值")
    private String reportedValue;

    private String roundingRule;
    private String formula;

    private BigDecimal lowerLimit;
    private BigDecimal upperLimit;
    private String specificationVal;

    @Excel(name = "单项结论")
    private String resultConclusion;

    private Integer isOos;
    private Integer isOot;

    private Long testerId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date testDate;

    private Long reviewerId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date reviewDate;

    private Long instrumentId;
    private String reagentLot;
    private String standardLot;
    private String testCondition;

    public Long getResultId() { return resultId; }
    public void setResultId(Long resultId) { this.resultId = resultId; }
    public Long getTestOrderId() { return testOrderId; }
    public void setTestOrderId(Long testOrderId) { this.testOrderId = testOrderId; }
    public Long getSpecLineId() { return specLineId; }
    public void setSpecLineId(Long specLineId) { this.specLineId = specLineId; }
    public String getTestItemName() { return testItemName; }
    public void setTestItemName(String testItemName) { this.testItemName = testItemName; }
    public Long getMethodId() { return methodId; }
    public void setMethodId(Long methodId) { this.methodId = methodId; }
    public String getTestItemType() { return testItemType; }
    public void setTestItemType(String testItemType) { this.testItemType = testItemType; }
    public String getOriginalValue() { return originalValue; }
    public void setOriginalValue(String originalValue) { this.originalValue = originalValue; }
    public String getInstrumentValue() { return instrumentValue; }
    public void setInstrumentValue(String instrumentValue) { this.instrumentValue = instrumentValue; }
    public BigDecimal getCalculatedValue() { return calculatedValue; }
    public void setCalculatedValue(BigDecimal calculatedValue) { this.calculatedValue = calculatedValue; }
    public String getReportedValue() { return reportedValue; }
    public void setReportedValue(String reportedValue) { this.reportedValue = reportedValue; }
    public String getRoundingRule() { return roundingRule; }
    public void setRoundingRule(String roundingRule) { this.roundingRule = roundingRule; }
    public String getFormula() { return formula; }
    public void setFormula(String formula) { this.formula = formula; }
    public BigDecimal getLowerLimit() { return lowerLimit; }
    public void setLowerLimit(BigDecimal lowerLimit) { this.lowerLimit = lowerLimit; }
    public BigDecimal getUpperLimit() { return upperLimit; }
    public void setUpperLimit(BigDecimal upperLimit) { this.upperLimit = upperLimit; }
    public String getSpecificationVal() { return specificationVal; }
    public void setSpecificationVal(String specificationVal) { this.specificationVal = specificationVal; }
    public String getResultConclusion() { return resultConclusion; }
    public void setResultConclusion(String resultConclusion) { this.resultConclusion = resultConclusion; }
    public Integer getIsOos() { return isOos; }
    public void setIsOos(Integer isOos) { this.isOos = isOos; }
    public Integer getIsOot() { return isOot; }
    public void setIsOot(Integer isOot) { this.isOot = isOot; }
    public Long getTesterId() { return testerId; }
    public void setTesterId(Long testerId) { this.testerId = testerId; }
    public Date getTestDate() { return testDate; }
    public void setTestDate(Date testDate) { this.testDate = testDate; }
    public Long getReviewerId() { return reviewerId; }
    public void setReviewerId(Long reviewerId) { this.reviewerId = reviewerId; }
    public Date getReviewDate() { return reviewDate; }
    public void setReviewDate(Date reviewDate) { this.reviewDate = reviewDate; }
    public Long getInstrumentId() { return instrumentId; }
    public void setInstrumentId(Long instrumentId) { this.instrumentId = instrumentId; }
    public String getReagentLot() { return reagentLot; }
    public void setReagentLot(String reagentLot) { this.reagentLot = reagentLot; }
    public String getStandardLot() { return standardLot; }
    public void setStandardLot(String standardLot) { this.standardLot = standardLot; }
    public String getTestCondition() { return testCondition; }
    public void setTestCondition(String testCondition) { this.testCondition = testCondition; }
}
