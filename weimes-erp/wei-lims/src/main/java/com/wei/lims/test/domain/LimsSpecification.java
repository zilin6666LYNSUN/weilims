package com.wei.lims.test.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wei.common.core.domain.BaseEntity;
import java.util.Date;

/**
 * 质量标准对象 lims_specification
 */
public class LimsSpecification extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long specificationId;
    private String specificationCode;
    private String specificationName;
    private String specificationType;
    private Long productId;
    private String version;
    private String pharmacopoeiaRef;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date effectiveDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expiryDate;
    private String status;

    public Long getSpecificationId() { return specificationId; }
    public void setSpecificationId(Long specificationId) { this.specificationId = specificationId; }
    public String getSpecificationCode() { return specificationCode; }
    public void setSpecificationCode(String specificationCode) { this.specificationCode = specificationCode; }
    public String getSpecificationName() { return specificationName; }
    public void setSpecificationName(String specificationName) { this.specificationName = specificationName; }
    public String getSpecificationType() { return specificationType; }
    public void setSpecificationType(String specificationType) { this.specificationType = specificationType; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getPharmacopoeiaRef() { return pharmacopoeiaRef; }
    public void setPharmacopoeiaRef(String pharmacopoeiaRef) { this.pharmacopoeiaRef = pharmacopoeiaRef; }
    public Date getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(Date effectiveDate) { this.effectiveDate = effectiveDate; }
    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
