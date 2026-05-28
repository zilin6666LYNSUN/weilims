package com.wei.lims.instrument.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 仪器维护保养记录对象 lims_instrument_maintenance
 */
public class LimsInstrumentMaintenance {
    private static final long serialVersionUID = 1L;

    private Long maintenanceId;
    private Long instrumentId;
    private String maintenanceType;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date maintenanceDate;
    private Long performedBy;
    private String description;
    private String status;

    public Long getMaintenanceId() { return maintenanceId; }
    public void setMaintenanceId(Long maintenanceId) { this.maintenanceId = maintenanceId; }
    public Long getInstrumentId() { return instrumentId; }
    public void setInstrumentId(Long instrumentId) { this.instrumentId = instrumentId; }
    public String getMaintenanceType() { return maintenanceType; }
    public void setMaintenanceType(String maintenanceType) { this.maintenanceType = maintenanceType; }
    public Date getMaintenanceDate() { return maintenanceDate; }
    public void setMaintenanceDate(Date maintenanceDate) { this.maintenanceDate = maintenanceDate; }
    public Long getPerformedBy() { return performedBy; }
    public void setPerformedBy(Long performedBy) { this.performedBy = performedBy; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
