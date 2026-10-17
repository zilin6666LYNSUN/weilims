package com.wei.lims.reagent.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wei.common.annotation.Excel;

/**
 * 标准品标定记录对象 lims_standard_qualification
 */
public class LimsStandardQualification {
    private static final long serialVersionUID = 1L;

    private Long qualId;

    private Long standardId;

    @Excel(name = "标定类型")
    private String qualType;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date qualDate;

    @Excel(name = "标定方法")
    private String qualMethod;

    @Excel(name = "测试结果")
    private String testResult;

    @Excel(name = "结论")
    private String qualifiedConclusion;

    private Long qualifiedBy;

    private Long approvedBy;

    @Excel(name = "状态")
    private String status;

    public Long getQualId() { return qualId; }
    public void setQualId(Long qualId) { this.qualId = qualId; }
    public Long getStandardId() { return standardId; }
    public void setStandardId(Long standardId) { this.standardId = standardId; }
    public String getQualType() { return qualType; }
    public void setQualType(String qualType) { this.qualType = qualType; }
    public Date getQualDate() { return qualDate; }
    public void setQualDate(Date qualDate) { this.qualDate = qualDate; }
    public String getQualMethod() { return qualMethod; }
    public void setQualMethod(String qualMethod) { this.qualMethod = qualMethod; }
    public String getTestResult() { return testResult; }
    public void setTestResult(String testResult) { this.testResult = testResult; }
    public String getQualifiedConclusion() { return qualifiedConclusion; }
    public void setQualifiedConclusion(String qualifiedConclusion) { this.qualifiedConclusion = qualifiedConclusion; }
    public Long getQualifiedBy() { return qualifiedBy; }
    public void setQualifiedBy(Long qualifiedBy) { this.qualifiedBy = qualifiedBy; }
    public Long getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
