package com.wei.lims.report.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wei.common.annotation.Excel;
import com.wei.common.core.domain.BaseEntity;

/**
 * 数据校验规则对象 lims_data_validation_rule
 */
public class LimsDataValidationRule extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long ruleId;

    @Excel(name = "规则编号")
    private String ruleCode;

    @Excel(name = "规则名称")
    private String ruleName;

    @Excel(name = "规则类型")
    private String ruleType;

    private Long specLineId;

    @Excel(name = "检测项名称")
    private String testItemName;

    private String ruleExpression;
    private String ruleParams;

    @Excel(name = "错误级别")
    private String errorLevel;

    @Excel(name = "错误提示")
    private String errorMessage;

    private Integer isEnabled;
    private String status;

    public Long getRuleId() { return ruleId; }
    public void setRuleId(Long ruleId) { this.ruleId = ruleId; }
    public String getRuleCode() { return ruleCode; }
    public void setRuleCode(String ruleCode) { this.ruleCode = ruleCode; }
    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }
    public String getRuleType() { return ruleType; }
    public void setRuleType(String ruleType) { this.ruleType = ruleType; }
    public Long getSpecLineId() { return specLineId; }
    public void setSpecLineId(Long specLineId) { this.specLineId = specLineId; }
    public String getTestItemName() { return testItemName; }
    public void setTestItemName(String testItemName) { this.testItemName = testItemName; }
    public String getRuleExpression() { return ruleExpression; }
    public void setRuleExpression(String ruleExpression) { this.ruleExpression = ruleExpression; }
    public String getRuleParams() { return ruleParams; }
    public void setRuleParams(String ruleParams) { this.ruleParams = ruleParams; }
    public String getErrorLevel() { return errorLevel; }
    public void setErrorLevel(String errorLevel) { this.errorLevel = errorLevel; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public Integer getIsEnabled() { return isEnabled; }
    public void setIsEnabled(Integer isEnabled) { this.isEnabled = isEnabled; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
