package com.wei.lims.test.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wei.common.core.domain.BaseEntity;
import java.util.Date;

/**
 * 检验方法对象 lims_test_method
 */
public class LimsTestMethod extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long methodId;
    private String methodCode;
    private String methodName;
    private String methodVersion;
    private String pharmacopoeiaRef;
    private String methodType;
    private String methodCategory;
    private String validationStatus;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date validatedDate;
    private Long approvedBy;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date effectiveDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expiryDate;

    public Long getMethodId() { return methodId; }
    public void setMethodId(Long methodId) { this.methodId = methodId; }
    public String getMethodCode() { return methodCode; }
    public void setMethodCode(String methodCode) { this.methodCode = methodCode; }
    public String getMethodName() { return methodName; }
    public void setMethodName(String methodName) { this.methodName = methodName; }
    public String getMethodVersion() { return methodVersion; }
    public void setMethodVersion(String methodVersion) { this.methodVersion = methodVersion; }
    public String getPharmacopoeiaRef() { return pharmacopoeiaRef; }
    public void setPharmacopoeiaRef(String pharmacopoeiaRef) { this.pharmacopoeiaRef = pharmacopoeiaRef; }
    public String getMethodType() { return methodType; }
    public void setMethodType(String methodType) { this.methodType = methodType; }
    public String getMethodCategory() { return methodCategory; }
    public void setMethodCategory(String methodCategory) { this.methodCategory = methodCategory; }
    public String getValidationStatus() { return validationStatus; }
    public void setValidationStatus(String validationStatus) { this.validationStatus = validationStatus; }
    public Date getValidatedDate() { return validatedDate; }
    public void setValidatedDate(Date validatedDate) { this.validatedDate = validatedDate; }
    public Long getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(Date effectiveDate) { this.effectiveDate = effectiveDate; }
    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }
}
