package com.wei.lims.report.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wei.common.core.domain.BaseEntity;

/**
 * 数据校验日志对象 lims_data_validation_log
 */
public class LimsDataValidationLog {
    private static final long serialVersionUID = 1L;

    private Long logId;
    private Long testOrderId;
    private Long resultId;
    private Long ruleId;
    private String ruleCode;

    /** PASS/FAIL/WARNING */
    private String validationStatus;
    private String actualValue;
    private String expectedValue;
    private String errorMessage;
    private String validatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date validatedAt;

    public Long getLogId() { return logId; }
    public void setLogId(Long logId) { this.logId = logId; }
    public Long getTestOrderId() { return testOrderId; }
    public void setTestOrderId(Long testOrderId) { this.testOrderId = testOrderId; }
    public Long getResultId() { return resultId; }
    public void setResultId(Long resultId) { this.resultId = resultId; }
    public Long getRuleId() { return ruleId; }
    public void setRuleId(Long ruleId) { this.ruleId = ruleId; }
    public String getRuleCode() { return ruleCode; }
    public void setRuleCode(String ruleCode) { this.ruleCode = ruleCode; }
    public String getValidationStatus() { return validationStatus; }
    public void setValidationStatus(String validationStatus) { this.validationStatus = validationStatus; }
    public String getActualValue() { return actualValue; }
    public void setActualValue(String actualValue) { this.actualValue = actualValue; }
    public String getExpectedValue() { return expectedValue; }
    public void setExpectedValue(String expectedValue) { this.expectedValue = expectedValue; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public String getValidatedBy() { return validatedBy; }
    public void setValidatedBy(String validatedBy) { this.validatedBy = validatedBy; }
    public Date getValidatedAt() { return validatedAt; }
    public void setValidatedAt(Date validatedAt) { this.validatedAt = validatedAt; }
}
