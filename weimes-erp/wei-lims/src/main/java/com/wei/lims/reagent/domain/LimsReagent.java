package com.wei.lims.reagent.domain;

import com.wei.common.annotation.Excel;
import com.wei.common.core.domain.BaseEntity;

/**
 * 试剂对象 lims_reagent
 */
public class LimsReagent extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long reagentId;

    @Excel(name = "试剂编号")
    private String reagentCode;

    @Excel(name = "试剂名称")
    private String reagentName;

    @Excel(name = "试剂类型")
    private String reagentType;

    @Excel(name = "CAS号")
    private String casNumber;

    @Excel(name = "生产商")
    private String manufacturer;

    @Excel(name = "危险等级")
    private String hazardLevel;

    @Excel(name = "存储条件")
    private String storageCondition;

    @Excel(name = "状态")
    private String status;

    public Long getReagentId() { return reagentId; }
    public void setReagentId(Long reagentId) { this.reagentId = reagentId; }
    public String getReagentCode() { return reagentCode; }
    public void setReagentCode(String reagentCode) { this.reagentCode = reagentCode; }
    public String getReagentName() { return reagentName; }
    public void setReagentName(String reagentName) { this.reagentName = reagentName; }
    public String getReagentType() { return reagentType; }
    public void setReagentType(String reagentType) { this.reagentType = reagentType; }
    public String getCasNumber() { return casNumber; }
    public void setCasNumber(String casNumber) { this.casNumber = casNumber; }
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public String getHazardLevel() { return hazardLevel; }
    public void setHazardLevel(String hazardLevel) { this.hazardLevel = hazardLevel; }
    public String getStorageCondition() { return storageCondition; }
    public void setStorageCondition(String storageCondition) { this.storageCondition = storageCondition; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
