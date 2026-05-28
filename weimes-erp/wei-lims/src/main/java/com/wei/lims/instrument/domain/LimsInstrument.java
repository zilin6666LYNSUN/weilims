package com.wei.lims.instrument.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wei.common.annotation.Excel;
import com.wei.common.core.domain.BaseEntity;

/**
 * 仪器台账对象 lims_instrument
 */
public class LimsInstrument extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long instrumentId;

    @Excel(name = "仪器编号")
    private String instrumentCode;

    @Excel(name = "仪器名称")
    private String instrumentName;

    private Long instrumentTypeId;
    private String instrumentTypeCode;
    private String instrumentTypeName;

    @Excel(name = "品牌")
    private String brand;

    @Excel(name = "规格型号")
    private String model;

    @Excel(name = "出厂编号")
    private String serialNumber;

    @Excel(name = "资产编号")
    private String assetNumber;

    @Excel(name = "生产厂家")
    private String manufacturer;

    private Long labId;
    private String labName;

    @Excel(name = "安装位置")
    private String location;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date purchaseDate;

    private String iqStatus;
    private String oqStatus;
    private String pqStatus;

    @Excel(name = "校准周期(天)")
    private Integer calibrationCycle;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date lastCalibration;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date nextCalibration;

    @Excel(name = "校准状态")
    private String calibrationStatus;

    private Integer maintenanceCycle;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date lastMaintenance;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date nextMaintenance;

    private String interfaceType;

    @Excel(name = "状态")
    private String status;

    public Long getInstrumentId() { return instrumentId; }
    public void setInstrumentId(Long instrumentId) { this.instrumentId = instrumentId; }
    public String getInstrumentCode() { return instrumentCode; }
    public void setInstrumentCode(String instrumentCode) { this.instrumentCode = instrumentCode; }
    public String getInstrumentName() { return instrumentName; }
    public void setInstrumentName(String instrumentName) { this.instrumentName = instrumentName; }
    public Long getInstrumentTypeId() { return instrumentTypeId; }
    public void setInstrumentTypeId(Long instrumentTypeId) { this.instrumentTypeId = instrumentTypeId; }
    public String getInstrumentTypeCode() { return instrumentTypeCode; }
    public void setInstrumentTypeCode(String instrumentTypeCode) { this.instrumentTypeCode = instrumentTypeCode; }
    public String getInstrumentTypeName() { return instrumentTypeName; }
    public void setInstrumentTypeName(String instrumentTypeName) { this.instrumentTypeName = instrumentTypeName; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    public String getAssetNumber() { return assetNumber; }
    public void setAssetNumber(String assetNumber) { this.assetNumber = assetNumber; }
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public Long getLabId() { return labId; }
    public void setLabId(Long labId) { this.labId = labId; }
    public String getLabName() { return labName; }
    public void setLabName(String labName) { this.labName = labName; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Date getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(Date purchaseDate) { this.purchaseDate = purchaseDate; }
    public String getIqStatus() { return iqStatus; }
    public void setIqStatus(String iqStatus) { this.iqStatus = iqStatus; }
    public String getOqStatus() { return oqStatus; }
    public void setOqStatus(String oqStatus) { this.oqStatus = oqStatus; }
    public String getPqStatus() { return pqStatus; }
    public void setPqStatus(String pqStatus) { this.pqStatus = pqStatus; }
    public Integer getCalibrationCycle() { return calibrationCycle; }
    public void setCalibrationCycle(Integer calibrationCycle) { this.calibrationCycle = calibrationCycle; }
    public Date getLastCalibration() { return lastCalibration; }
    public void setLastCalibration(Date lastCalibration) { this.lastCalibration = lastCalibration; }
    public Date getNextCalibration() { return nextCalibration; }
    public void setNextCalibration(Date nextCalibration) { this.nextCalibration = nextCalibration; }
    public String getCalibrationStatus() { return calibrationStatus; }
    public void setCalibrationStatus(String calibrationStatus) { this.calibrationStatus = calibrationStatus; }
    public Integer getMaintenanceCycle() { return maintenanceCycle; }
    public void setMaintenanceCycle(Integer maintenanceCycle) { this.maintenanceCycle = maintenanceCycle; }
    public Date getLastMaintenance() { return lastMaintenance; }
    public void setLastMaintenance(Date lastMaintenance) { this.lastMaintenance = lastMaintenance; }
    public Date getNextMaintenance() { return nextMaintenance; }
    public void setNextMaintenance(Date nextMaintenance) { this.nextMaintenance = nextMaintenance; }
    public String getInterfaceType() { return interfaceType; }
    public void setInterfaceType(String interfaceType) { this.interfaceType = interfaceType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
