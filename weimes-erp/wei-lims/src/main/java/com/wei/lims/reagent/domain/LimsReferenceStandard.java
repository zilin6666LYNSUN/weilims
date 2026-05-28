package com.wei.lims.reagent.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 标准品对象 lims_reference_standard
 */
public class LimsReferenceStandard {
    private static final long serialVersionUID = 1L;

    private Long standardId;
    private String standardCode;
    private String standardName;
    private String standardType;
    private String source;
    private String catalogNumber;
    private String lotNumber;
    private BigDecimal potency;
    private String potencyUnit;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date receivedDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date openedDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expireDate;
    private String storageCondition;
    private String storageLocation;
    private BigDecimal remainingQuantity;
    private String unit;
    private String qualificationStatus;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date qualificationDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date nextQualification;
    private String coaFile;
    private String structureFile;
    private String casNumber;
    private String status;

    public Long getStandardId() { return standardId; }
    public void setStandardId(Long standardId) { this.standardId = standardId; }
    public String getStandardCode() { return standardCode; }
    public void setStandardCode(String standardCode) { this.standardCode = standardCode; }
    public String getStandardName() { return standardName; }
    public void setStandardName(String standardName) { this.standardName = standardName; }
    public String getStandardType() { return standardType; }
    public void setStandardType(String standardType) { this.standardType = standardType; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getCatalogNumber() { return catalogNumber; }
    public void setCatalogNumber(String catalogNumber) { this.catalogNumber = catalogNumber; }
    public String getLotNumber() { return lotNumber; }
    public void setLotNumber(String lotNumber) { this.lotNumber = lotNumber; }
    public BigDecimal getPotency() { return potency; }
    public void setPotency(BigDecimal potency) { this.potency = potency; }
    public String getPotencyUnit() { return potencyUnit; }
    public void setPotencyUnit(String potencyUnit) { this.potencyUnit = potencyUnit; }
    public Date getReceivedDate() { return receivedDate; }
    public void setReceivedDate(Date receivedDate) { this.receivedDate = receivedDate; }
    public Date getOpenedDate() { return openedDate; }
    public void setOpenedDate(Date openedDate) { this.openedDate = openedDate; }
    public Date getExpireDate() { return expireDate; }
    public void setExpireDate(Date expireDate) { this.expireDate = expireDate; }
    public String getStorageCondition() { return storageCondition; }
    public void setStorageCondition(String storageCondition) { this.storageCondition = storageCondition; }
    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }
    public BigDecimal getRemainingQuantity() { return remainingQuantity; }
    public void setRemainingQuantity(BigDecimal remainingQuantity) { this.remainingQuantity = remainingQuantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getQualificationStatus() { return qualificationStatus; }
    public void setQualificationStatus(String qualificationStatus) { this.qualificationStatus = qualificationStatus; }
    public Date getQualificationDate() { return qualificationDate; }
    public void setQualificationDate(Date qualificationDate) { this.qualificationDate = qualificationDate; }
    public Date getNextQualification() { return nextQualification; }
    public void setNextQualification(Date nextQualification) { this.nextQualification = nextQualification; }
    public String getCoaFile() { return coaFile; }
    public void setCoaFile(String coaFile) { this.coaFile = coaFile; }
    public String getStructureFile() { return structureFile; }
    public void setStructureFile(String structureFile) { this.structureFile = structureFile; }
    public String getCasNumber() { return casNumber; }
    public void setCasNumber(String casNumber) { this.casNumber = casNumber; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
