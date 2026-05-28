package com.wei.lims.test.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wei.common.annotation.Excel;
import com.wei.common.core.domain.BaseEntity;

/**
 * 检验订单对象 lims_test_order
 */
public class LimsTestOrder extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long testOrderId;

    @Excel(name = "检验单编号")
    private String testOrderCode;

    @Excel(name = "检验单名称")
    private String testOrderName;

    private Long sampleId;
    private String sampleCode;
    private Long specificationId;
    private Long testTemplateId;

    @Excel(name = "来源类型")
    private String sourceDocType;

    private Long sourceDocId;

    @Excel(name = "来源单据编号")
    private String sourceDocCode;

    @Excel(name = "检验类型")
    private String testType;

    @Excel(name = "优先级")
    private String priority;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date plannedStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date plannedEndDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date actualStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date actualEndDate;

    private Long testerId;
    private Long reviewerId;
    private Long approverId;

    @Excel(name = "检验结论")
    private String checkResult;

    private String conclusion;

    @Excel(name = "报告编号")
    private String reportNo;

    private String procInstId;

    @Excel(name = "状态")
    private String status;

    public Long getTestOrderId() { return testOrderId; }
    public void setTestOrderId(Long testOrderId) { this.testOrderId = testOrderId; }
    public String getTestOrderCode() { return testOrderCode; }
    public void setTestOrderCode(String testOrderCode) { this.testOrderCode = testOrderCode; }
    public String getTestOrderName() { return testOrderName; }
    public void setTestOrderName(String testOrderName) { this.testOrderName = testOrderName; }
    public Long getSampleId() { return sampleId; }
    public void setSampleId(Long sampleId) { this.sampleId = sampleId; }
    public String getSampleCode() { return sampleCode; }
    public void setSampleCode(String sampleCode) { this.sampleCode = sampleCode; }
    public Long getSpecificationId() { return specificationId; }
    public void setSpecificationId(Long specificationId) { this.specificationId = specificationId; }
    public Long getTestTemplateId() { return testTemplateId; }
    public void setTestTemplateId(Long testTemplateId) { this.testTemplateId = testTemplateId; }
    public String getSourceDocType() { return sourceDocType; }
    public void setSourceDocType(String sourceDocType) { this.sourceDocType = sourceDocType; }
    public Long getSourceDocId() { return sourceDocId; }
    public void setSourceDocId(Long sourceDocId) { this.sourceDocId = sourceDocId; }
    public String getSourceDocCode() { return sourceDocCode; }
    public void setSourceDocCode(String sourceDocCode) { this.sourceDocCode = sourceDocCode; }
    public String getTestType() { return testType; }
    public void setTestType(String testType) { this.testType = testType; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public Date getPlannedStartDate() { return plannedStartDate; }
    public void setPlannedStartDate(Date plannedStartDate) { this.plannedStartDate = plannedStartDate; }
    public Date getPlannedEndDate() { return plannedEndDate; }
    public void setPlannedEndDate(Date plannedEndDate) { this.plannedEndDate = plannedEndDate; }
    public Date getActualStartDate() { return actualStartDate; }
    public void setActualStartDate(Date actualStartDate) { this.actualStartDate = actualStartDate; }
    public Date getActualEndDate() { return actualEndDate; }
    public void setActualEndDate(Date actualEndDate) { this.actualEndDate = actualEndDate; }
    public Long getTesterId() { return testerId; }
    public void setTesterId(Long testerId) { this.testerId = testerId; }
    public Long getReviewerId() { return reviewerId; }
    public void setReviewerId(Long reviewerId) { this.reviewerId = reviewerId; }
    public Long getApproverId() { return approverId; }
    public void setApproverId(Long approverId) { this.approverId = approverId; }
    public String getCheckResult() { return checkResult; }
    public void setCheckResult(String checkResult) { this.checkResult = checkResult; }
    public String getConclusion() { return conclusion; }
    public void setConclusion(String conclusion) { this.conclusion = conclusion; }
    public String getReportNo() { return reportNo; }
    public void setReportNo(String reportNo) { this.reportNo = reportNo; }
    public String getProcInstId() { return procInstId; }
    public void setProcInstId(String procInstId) { this.procInstId = procInstId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
