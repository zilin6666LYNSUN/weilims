package com.wei.lims.reagent.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wei.common.annotation.Excel;

/**
 * 培养基对象 lims_culture_media
 */
public class LimsCultureMedia {
    private static final long serialVersionUID = 1L;

    private Long mediaId;

    @Excel(name = "培养基编号")
    private String mediaCode;

    @Excel(name = "培养基名称")
    private String mediaName;

    @Excel(name = "类型")
    private String mediaType;

    @Excel(name = "生产商")
    private String manufacturer;

    @Excel(name = "批号")
    private String lotNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date preparationDate;

    @Excel(name = "灭菌方式")
    private String sterilizationMethod;

    @Excel(name = "无菌检查")
    private Integer sterilityTest;

    @Excel(name = "生长促进试验")
    private Integer growthPromotionTest;

    @Excel(name = "生长试验结果")
    private String growthTestResult;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expireDate;

    @Excel(name = "储存条件")
    private String storageCondition;

    @Excel(name = "状态")
    private String status;

    public Long getMediaId() { return mediaId; }
    public void setMediaId(Long mediaId) { this.mediaId = mediaId; }
    public String getMediaCode() { return mediaCode; }
    public void setMediaCode(String mediaCode) { this.mediaCode = mediaCode; }
    public String getMediaName() { return mediaName; }
    public void setMediaName(String mediaName) { this.mediaName = mediaName; }
    public String getMediaType() { return mediaType; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public String getLotNumber() { return lotNumber; }
    public void setLotNumber(String lotNumber) { this.lotNumber = lotNumber; }
    public Date getPreparationDate() { return preparationDate; }
    public void setPreparationDate(Date preparationDate) { this.preparationDate = preparationDate; }
    public String getSterilizationMethod() { return sterilizationMethod; }
    public void setSterilizationMethod(String sterilizationMethod) { this.sterilizationMethod = sterilizationMethod; }
    public Integer getSterilityTest() { return sterilityTest; }
    public void setSterilityTest(Integer sterilityTest) { this.sterilityTest = sterilityTest; }
    public Integer getGrowthPromotionTest() { return growthPromotionTest; }
    public void setGrowthPromotionTest(Integer growthPromotionTest) { this.growthPromotionTest = growthPromotionTest; }
    public String getGrowthTestResult() { return growthTestResult; }
    public void setGrowthTestResult(String growthTestResult) { this.growthTestResult = growthTestResult; }
    public Date getExpireDate() { return expireDate; }
    public void setExpireDate(Date expireDate) { this.expireDate = expireDate; }
    public String getStorageCondition() { return storageCondition; }
    public void setStorageCondition(String storageCondition) { this.storageCondition = storageCondition; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
