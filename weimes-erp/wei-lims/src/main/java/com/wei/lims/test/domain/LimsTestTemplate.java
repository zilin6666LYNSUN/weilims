package com.wei.lims.test.domain;

import com.wei.common.annotation.Excel;
import com.wei.common.core.domain.BaseEntity;

/**
 * 检验模板对象 lims_test_template
 */
public class LimsTestTemplate extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long templateId;

    @Excel(name = "模板编号")
    private String templateCode;

    @Excel(name = "模板名称")
    private String templateName;

    @Excel(name = "检验类型")
    private String testType;

    private Long productId;

    @Excel(name = "产品名称")
    private String productName;

    private Long specificationId;

    @Excel(name = "状态: DRAFT-草稿/ACTIVE-生效/OBSOLETE-废止")
    private String status;

    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }
    public String getTemplateCode() { return templateCode; }
    public void setTemplateCode(String templateCode) { this.templateCode = templateCode; }
    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }
    public String getTestType() { return testType; }
    public void setTestType(String testType) { this.testType = testType; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Long getSpecificationId() { return specificationId; }
    public void setSpecificationId(Long specificationId) { this.specificationId = specificationId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
