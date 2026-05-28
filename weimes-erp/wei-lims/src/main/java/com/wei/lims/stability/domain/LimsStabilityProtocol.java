package com.wei.lims.stability.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wei.common.core.domain.BaseEntity;
import java.util.Date;

public class LimsStabilityProtocol extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long protocolId;
    private String protocolCode;
    private Long productId;
    private String productName;
    private String batchNo;
    private String studyType;
    private String storageCondition;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    private String testPoints;
    private Long specificationId;
    private Long testTemplateId;
    private String status;
    private String procInstId;

    public Long getProtocolId() { return protocolId; }
    public void setProtocolId(Long protocolId) { this.protocolId = protocolId; }
    public String getProtocolCode() { return protocolCode; }
    public void setProtocolCode(String protocolCode) { this.protocolCode = protocolCode; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public String getStudyType() { return studyType; }
    public void setStudyType(String studyType) { this.studyType = studyType; }
    public String getStorageCondition() { return storageCondition; }
    public void setStorageCondition(String storageCondition) { this.storageCondition = storageCondition; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public String getTestPoints() { return testPoints; }
    public void setTestPoints(String testPoints) { this.testPoints = testPoints; }
    public Long getSpecificationId() { return specificationId; }
    public void setSpecificationId(Long specificationId) { this.specificationId = specificationId; }
    public Long getTestTemplateId() { return testTemplateId; }
    public void setTestTemplateId(Long testTemplateId) { this.testTemplateId = testTemplateId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getProcInstId() { return procInstId; }
    public void setProcInstId(String procInstId) { this.procInstId = procInstId; }
}
