package com.wei.lims.report.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wei.common.annotation.Excel;
import com.wei.common.core.domain.BaseEntity;

/**
 * 检验报告对象 lims_report
 * GMP合规：支持编制-复核-批准-放行四步审批流程
 */
public class LimsReport extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long reportId;

    @Excel(name = "报告编号")
    private String reportCode;

    @Excel(name = "报告类型: CMA/CNAS/INTERNAL")
    private String reportType;

    private Long templateId;

    private String templateVersion;

    @Excel(name = "业务类型")
    private String businessType;

    private Long businessId;

    @Excel(name = "报告标题")
    private String title;

    @Excel(name = "产品名称")
    private String productName;

    @Excel(name = "批号")
    private String batchNo;

    private String reportData;

    private String pdfPath;

    private String pdfHash;

    private Long generatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date generatedTime;

    private Long reviewedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reviewedTime;

    private String reviewComment;

    private Long approvedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date approvedTime;

    private String approveComment;

    private Long releasedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date releasedTime;

    private Integer printedCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastPrintedTime;

    @Excel(name = "状态")
    private String status;

    private String procInstId;

    private String conclusion;

    private String voidReason;

    private Integer version;

    private Long parentReportId;

    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }
    public String getReportCode() { return reportCode; }
    public void setReportCode(String reportCode) { this.reportCode = reportCode; }
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }
    public String getTemplateVersion() { return templateVersion; }
    public void setTemplateVersion(String templateVersion) { this.templateVersion = templateVersion; }
    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }
    public Long getBusinessId() { return businessId; }
    public void setBusinessId(Long businessId) { this.businessId = businessId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public String getReportData() { return reportData; }
    public void setReportData(String reportData) { this.reportData = reportData; }
    public String getPdfPath() { return pdfPath; }
    public void setPdfPath(String pdfPath) { this.pdfPath = pdfPath; }
    public String getPdfHash() { return pdfHash; }
    public void setPdfHash(String pdfHash) { this.pdfHash = pdfHash; }
    public Long getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(Long generatedBy) { this.generatedBy = generatedBy; }
    public Date getGeneratedTime() { return generatedTime; }
    public void setGeneratedTime(Date generatedTime) { this.generatedTime = generatedTime; }
    public Long getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(Long reviewedBy) { this.reviewedBy = reviewedBy; }
    public Date getReviewedTime() { return reviewedTime; }
    public void setReviewedTime(Date reviewedTime) { this.reviewedTime = reviewedTime; }
    public String getReviewComment() { return reviewComment; }
    public void setReviewComment(String reviewComment) { this.reviewComment = reviewComment; }
    public Long getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }
    public Date getApprovedTime() { return approvedTime; }
    public void setApprovedTime(Date approvedTime) { this.approvedTime = approvedTime; }
    public String getApproveComment() { return approveComment; }
    public void setApproveComment(String approveComment) { this.approveComment = approveComment; }
    public Long getReleasedBy() { return releasedBy; }
    public void setReleasedBy(Long releasedBy) { this.releasedBy = releasedBy; }
    public Date getReleasedTime() { return releasedTime; }
    public void setReleasedTime(Date releasedTime) { this.releasedTime = releasedTime; }
    public Integer getPrintedCount() { return printedCount; }
    public void setPrintedCount(Integer printedCount) { this.printedCount = printedCount; }
    public Date getLastPrintedTime() { return lastPrintedTime; }
    public void setLastPrintedTime(Date lastPrintedTime) { this.lastPrintedTime = lastPrintedTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getProcInstId() { return procInstId; }
    public void setProcInstId(String procInstId) { this.procInstId = procInstId; }
    public String getConclusion() { return conclusion; }
    public void setConclusion(String conclusion) { this.conclusion = conclusion; }
    public String getVoidReason() { return voidReason; }
    public void setVoidReason(String voidReason) { this.voidReason = voidReason; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public Long getParentReportId() { return parentReportId; }
    public void setParentReportId(Long parentReportId) { this.parentReportId = parentReportId; }
}
