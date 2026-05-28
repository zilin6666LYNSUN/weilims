package com.wei.lims.reagent.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 菌种对象 lims_culture_collection
 */
public class LimsCultureCollection {
    private static final long serialVersionUID = 1L;

    private Long cultureId;
    private String cultureCode;
    private String cultureName;
    private String strainNumber;
    private String source;
    private String storageMethod;
    private String storageLocation;
    private Integer passageCount;
    private Integer maxPassage;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date receivedDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expireDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date viabilityTestDate;
    private String viabilityResult;
    private String status;

    public Long getCultureId() { return cultureId; }
    public void setCultureId(Long cultureId) { this.cultureId = cultureId; }
    public String getCultureCode() { return cultureCode; }
    public void setCultureCode(String cultureCode) { this.cultureCode = cultureCode; }
    public String getCultureName() { return cultureName; }
    public void setCultureName(String cultureName) { this.cultureName = cultureName; }
    public String getStrainNumber() { return strainNumber; }
    public void setStrainNumber(String strainNumber) { this.strainNumber = strainNumber; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getStorageMethod() { return storageMethod; }
    public void setStorageMethod(String storageMethod) { this.storageMethod = storageMethod; }
    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }
    public Integer getPassageCount() { return passageCount; }
    public void setPassageCount(Integer passageCount) { this.passageCount = passageCount; }
    public Integer getMaxPassage() { return maxPassage; }
    public void setMaxPassage(Integer maxPassage) { this.maxPassage = maxPassage; }
    public Date getReceivedDate() { return receivedDate; }
    public void setReceivedDate(Date receivedDate) { this.receivedDate = receivedDate; }
    public Date getExpireDate() { return expireDate; }
    public void setExpireDate(Date expireDate) { this.expireDate = expireDate; }
    public Date getViabilityTestDate() { return viabilityTestDate; }
    public void setViabilityTestDate(Date viabilityTestDate) { this.viabilityTestDate = viabilityTestDate; }
    public String getViabilityResult() { return viabilityResult; }
    public void setViabilityResult(String viabilityResult) { this.viabilityResult = viabilityResult; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
