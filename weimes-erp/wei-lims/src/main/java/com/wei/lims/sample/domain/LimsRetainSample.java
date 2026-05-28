package com.wei.lims.sample.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 留样管理对象 lims_retain_sample
 */
public class LimsRetainSample {
    private static final long serialVersionUID = 1L;

    private Long retainId;
    private String retainCode;
    private Long sampleId;
    private String productName;
    private String batchNo;
    private String retainType;
    private BigDecimal retainQuantity;
    private BigDecimal remainQuantity;
    private String storageLocation;
    private String storageCondition;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date retainDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expireDate;
    private String observationCycle;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date lastObservation;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date nextObservation;
    private String observationResult;
    private String disposeType;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date disposeDate;
    private String disposeApprover;
    private String status;

    public Long getRetainId() { return retainId; }
    public void setRetainId(Long retainId) { this.retainId = retainId; }
    public String getRetainCode() { return retainCode; }
    public void setRetainCode(String retainCode) { this.retainCode = retainCode; }
    public Long getSampleId() { return sampleId; }
    public void setSampleId(Long sampleId) { this.sampleId = sampleId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public String getRetainType() { return retainType; }
    public void setRetainType(String retainType) { this.retainType = retainType; }
    public BigDecimal getRetainQuantity() { return retainQuantity; }
    public void setRetainQuantity(BigDecimal retainQuantity) { this.retainQuantity = retainQuantity; }
    public BigDecimal getRemainQuantity() { return remainQuantity; }
    public void setRemainQuantity(BigDecimal remainQuantity) { this.remainQuantity = remainQuantity; }
    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }
    public String getStorageCondition() { return storageCondition; }
    public void setStorageCondition(String storageCondition) { this.storageCondition = storageCondition; }
    public Date getRetainDate() { return retainDate; }
    public void setRetainDate(Date retainDate) { this.retainDate = retainDate; }
    public Date getExpireDate() { return expireDate; }
    public void setExpireDate(Date expireDate) { this.expireDate = expireDate; }
    public String getObservationCycle() { return observationCycle; }
    public void setObservationCycle(String observationCycle) { this.observationCycle = observationCycle; }
    public Date getLastObservation() { return lastObservation; }
    public void setLastObservation(Date lastObservation) { this.lastObservation = lastObservation; }
    public Date getNextObservation() { return nextObservation; }
    public void setNextObservation(Date nextObservation) { this.nextObservation = nextObservation; }
    public String getObservationResult() { return observationResult; }
    public void setObservationResult(String observationResult) { this.observationResult = observationResult; }
    public String getDisposeType() { return disposeType; }
    public void setDisposeType(String disposeType) { this.disposeType = disposeType; }
    public Date getDisposeDate() { return disposeDate; }
    public void setDisposeDate(Date disposeDate) { this.disposeDate = disposeDate; }
    public String getDisposeApprover() { return disposeApprover; }
    public void setDisposeApprover(String disposeApprover) { this.disposeApprover = disposeApprover; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
