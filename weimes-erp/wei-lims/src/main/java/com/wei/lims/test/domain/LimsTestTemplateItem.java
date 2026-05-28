package com.wei.lims.test.domain;

import com.wei.common.annotation.Excel;
import com.wei.common.core.domain.BaseEntity;

/**
 * 检验模板检测项对象 lims_test_template_item
 */
public class LimsTestTemplateItem extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long templateId;

    @Excel(name = "检测项名称")
    private String testItemName;

    @Excel(name = "检测项编码")
    private String testItemCode;

    private Long methodId;

    @Excel(name = "检测方法")
    private String methodName;

    private Long specLineId;

    @Excel(name = "检测项类型")
    private String testItemType;

    @Excel(name = "是否必检")
    private Integer isRequired;

    @Excel(name = "排序")
    private Integer sortOrder;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }
    public String getTestItemName() { return testItemName; }
    public void setTestItemName(String testItemName) { this.testItemName = testItemName; }
    public String getTestItemCode() { return testItemCode; }
    public void setTestItemCode(String testItemCode) { this.testItemCode = testItemCode; }
    public Long getMethodId() { return methodId; }
    public void setMethodId(Long methodId) { this.methodId = methodId; }
    public String getMethodName() { return methodName; }
    public void setMethodName(String methodName) { this.methodName = methodName; }
    public Long getSpecLineId() { return specLineId; }
    public void setSpecLineId(Long specLineId) { this.specLineId = specLineId; }
    public String getTestItemType() { return testItemType; }
    public void setTestItemType(String testItemType) { this.testItemType = testItemType; }
    public Integer getIsRequired() { return isRequired; }
    public void setIsRequired(Integer isRequired) { this.isRequired = isRequired; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
