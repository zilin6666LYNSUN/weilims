package com.wei.lims.sample.domain;

/**
 * 样品-检测项关联对象 lims_sample_test_item
 */
public class LimsSampleTestItem {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long sampleId;
    private Long testItemId;
    private String testItemCode;
    private String testItemName;
    private Long methodId;
    private Long specificationLineId;
    private Integer isMandatory;
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSampleId() { return sampleId; }
    public void setSampleId(Long sampleId) { this.sampleId = sampleId; }
    public Long getTestItemId() { return testItemId; }
    public void setTestItemId(Long testItemId) { this.testItemId = testItemId; }
    public String getTestItemCode() { return testItemCode; }
    public void setTestItemCode(String testItemCode) { this.testItemCode = testItemCode; }
    public String getTestItemName() { return testItemName; }
    public void setTestItemName(String testItemName) { this.testItemName = testItemName; }
    public Long getMethodId() { return methodId; }
    public void setMethodId(Long methodId) { this.methodId = methodId; }
    public Long getSpecificationLineId() { return specificationLineId; }
    public void setSpecificationLineId(Long specificationLineId) { this.specificationLineId = specificationLineId; }
    public Integer getIsMandatory() { return isMandatory; }
    public void setIsMandatory(Integer isMandatory) { this.isMandatory = isMandatory; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
