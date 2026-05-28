package com.wei.lims.sample.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wei.common.annotation.Excel;
import com.wei.common.core.domain.BaseEntity;

/**
 * 样品主对象 lims_sample
 */
public class LimsSample extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long sampleId;

    @Excel(name = "样品编号")
    private String sampleCode;

    @Excel(name = "样品名称")
    private String sampleName;

    @Excel(name = "样品类型")
    private String sampleType;

    @Excel(name = "样品来源")
    private String sampleSource;

    @Excel(name = "批号")
    private String batchNo;

    private Long productId;
    private String productCode;
    private String productName;

    private Long vendorId;
    private String vendorCode;
    private String vendorName;

    @Excel(name = "供应商批号")
    private String vendorBatch;

    @Excel(name = "规格型号")
    private String specification;

    @Excel(name = "接收数量")
    private BigDecimal quantityReceived;

    @Excel(name = "剩余数量")
    private BigDecimal quantityRemaining;

    @Excel(name = "单位")
    private String unitOfMeasure;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "效期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date expireDate;

    @Excel(name = "存储条件")
    private String storageCondition;

    @Excel(name = "存储位置")
    private String storageLocation;

    @Excel(name = "优先级")
    private String priority;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "接收日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date receiveDate;

    private Long samplerId;
    private Long inspectorId;
    private Long specificationId;
    private Long testTemplateId;

    @Excel(name = "状态")
    private String status;

    private String procInstId;

    private String attr1;
    private String attr2;
    private Long attr3;
    private Long attr4;

    public Long getSampleId() { return sampleId; }
    public void setSampleId(Long sampleId) { this.sampleId = sampleId; }
    public String getSampleCode() { return sampleCode; }
    public void setSampleCode(String sampleCode) { this.sampleCode = sampleCode; }
    public String getSampleName() { return sampleName; }
    public void setSampleName(String sampleName) { this.sampleName = sampleName; }
    public String getSampleType() { return sampleType; }
    public void setSampleType(String sampleType) { this.sampleType = sampleType; }
    public String getSampleSource() { return sampleSource; }
    public void setSampleSource(String sampleSource) { this.sampleSource = sampleSource; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }
    public String getVendorCode() { return vendorCode; }
    public void setVendorCode(String vendorCode) { this.vendorCode = vendorCode; }
    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }
    public String getVendorBatch() { return vendorBatch; }
    public void setVendorBatch(String vendorBatch) { this.vendorBatch = vendorBatch; }
    public String getSpecification() { return specification; }
    public void setSpecification(String specification) { this.specification = specification; }
    public BigDecimal getQuantityReceived() { return quantityReceived; }
    public void setQuantityReceived(BigDecimal quantityReceived) { this.quantityReceived = quantityReceived; }
    public BigDecimal getQuantityRemaining() { return quantityRemaining; }
    public void setQuantityRemaining(BigDecimal quantityRemaining) { this.quantityRemaining = quantityRemaining; }
    public String getUnitOfMeasure() { return unitOfMeasure; }
    public void setUnitOfMeasure(String unitOfMeasure) { this.unitOfMeasure = unitOfMeasure; }
    public Date getExpireDate() { return expireDate; }
    public void setExpireDate(Date expireDate) { this.expireDate = expireDate; }
    public String getStorageCondition() { return storageCondition; }
    public void setStorageCondition(String storageCondition) { this.storageCondition = storageCondition; }
    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public Date getReceiveDate() { return receiveDate; }
    public void setReceiveDate(Date receiveDate) { this.receiveDate = receiveDate; }
    public Long getSamplerId() { return samplerId; }
    public void setSamplerId(Long samplerId) { this.samplerId = samplerId; }
    public Long getInspectorId() { return inspectorId; }
    public void setInspectorId(Long inspectorId) { this.inspectorId = inspectorId; }
    public Long getSpecificationId() { return specificationId; }
    public void setSpecificationId(Long specificationId) { this.specificationId = specificationId; }
    public Long getTestTemplateId() { return testTemplateId; }
    public void setTestTemplateId(Long testTemplateId) { this.testTemplateId = testTemplateId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getProcInstId() { return procInstId; }
    public void setProcInstId(String procInstId) { this.procInstId = procInstId; }
    public String getAttr1() { return attr1; }
    public void setAttr1(String attr1) { this.attr1 = attr1; }
    public String getAttr2() { return attr2; }
    public void setAttr2(String attr2) { this.attr2 = attr2; }
    public Long getAttr3() { return attr3; }
    public void setAttr3(Long attr3) { this.attr3 = attr3; }
    public Long getAttr4() { return attr4; }
    public void setAttr4(Long attr4) { this.attr4 = attr4; }
}
