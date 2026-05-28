package com.wei.lims.test.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 微生物检测记录对象 lims_micro_test_record
 */
public class LimsMicroTestRecord {
    private static final long serialVersionUID = 1L;

    private Long recordId;
    private Long testOrderId;
    private String testType;
    private Long mediaId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date inoculationDate;
    private BigDecimal incubationTemp;
    private Integer incubationDays;
    private String positiveControl;
    private String negativeControl;
    private Long cultureId;
    private String resultDescription;
    private Integer colonyCount;
    private BigDecimal resultValue;
    private String specificationLimit;
    private String conclusion;
    private String status;

    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }
    public Long getTestOrderId() { return testOrderId; }
    public void setTestOrderId(Long testOrderId) { this.testOrderId = testOrderId; }
    public String getTestType() { return testType; }
    public void setTestType(String testType) { this.testType = testType; }
    public Long getMediaId() { return mediaId; }
    public void setMediaId(Long mediaId) { this.mediaId = mediaId; }
    public Date getInoculationDate() { return inoculationDate; }
    public void setInoculationDate(Date inoculationDate) { this.inoculationDate = inoculationDate; }
    public BigDecimal getIncubationTemp() { return incubationTemp; }
    public void setIncubationTemp(BigDecimal incubationTemp) { this.incubationTemp = incubationTemp; }
    public Integer getIncubationDays() { return incubationDays; }
    public void setIncubationDays(Integer incubationDays) { this.incubationDays = incubationDays; }
    public String getPositiveControl() { return positiveControl; }
    public void setPositiveControl(String positiveControl) { this.positiveControl = positiveControl; }
    public String getNegativeControl() { return negativeControl; }
    public void setNegativeControl(String negativeControl) { this.negativeControl = negativeControl; }
    public Long getCultureId() { return cultureId; }
    public void setCultureId(Long cultureId) { this.cultureId = cultureId; }
    public String getResultDescription() { return resultDescription; }
    public void setResultDescription(String resultDescription) { this.resultDescription = resultDescription; }
    public Integer getColonyCount() { return colonyCount; }
    public void setColonyCount(Integer colonyCount) { this.colonyCount = colonyCount; }
    public BigDecimal getResultValue() { return resultValue; }
    public void setResultValue(BigDecimal resultValue) { this.resultValue = resultValue; }
    public String getSpecificationLimit() { return specificationLimit; }
    public void setSpecificationLimit(String specificationLimit) { this.specificationLimit = specificationLimit; }
    public String getConclusion() { return conclusion; }
    public void setConclusion(String conclusion) { this.conclusion = conclusion; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
