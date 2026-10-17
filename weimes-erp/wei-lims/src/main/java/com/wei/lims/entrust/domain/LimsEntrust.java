package com.wei.lims.entrust.domain;

import java.util.Date;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 委托单对象 lims_entrust
 */
public class LimsEntrust {
    private static final long serialVersionUID = 1L;

    private Long entrustId;

    private String entrustCode;

    private Long customerId;

    private Long templateId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date entrustDate;

    private String sampleName;

    private String specification;

    private String batchNo;

    private BigDecimal quantity;

    private String unitOfMeasure;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date deliveryDate;

    private Integer reportRequired;

    private String reportFormat;

    private String priority;

    private String status;

    private Long reviewBy;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date reviewTime;

    private String reviewComment;

    private Long sampleId;

    private String remark;

    public Long getEntrustId() { return entrustId; }
    public void setEntrustId(Long entrustId) { this.entrustId = entrustId; }

    public String getEntrustCode() { return entrustCode; }
    public void setEntrustCode(String entrustCode) { this.entrustCode = entrustCode; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }

    public Date getEntrustDate() { return entrustDate; }
    public void setEntrustDate(Date entrustDate) { this.entrustDate = entrustDate; }

    public String getSampleName() { return sampleName; }
    public void setSampleName(String sampleName) { this.sampleName = sampleName; }

    public String getSpecification() { return specification; }
    public void setSpecification(String specification) { this.specification = specification; }

    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public String getUnitOfMeasure() { return unitOfMeasure; }
    public void setUnitOfMeasure(String unitOfMeasure) { this.unitOfMeasure = unitOfMeasure; }

    public Date getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(Date deliveryDate) { this.deliveryDate = deliveryDate; }

    public Integer getReportRequired() { return reportRequired; }
    public void setReportRequired(Integer reportRequired) { this.reportRequired = reportRequired; }

    public String getReportFormat() { return reportFormat; }
    public void setReportFormat(String reportFormat) { this.reportFormat = reportFormat; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getReviewBy() { return reviewBy; }
    public void setReviewBy(Long reviewBy) { this.reviewBy = reviewBy; }

    public Date getReviewTime() { return reviewTime; }
    public void setReviewTime(Date reviewTime) { this.reviewTime = reviewTime; }

    public String getReviewComment() { return reviewComment; }
    public void setReviewComment(String reviewComment) { this.reviewComment = reviewComment; }

    public Long getSampleId() { return sampleId; }
    public void setSampleId(Long sampleId) { this.sampleId = sampleId; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

}