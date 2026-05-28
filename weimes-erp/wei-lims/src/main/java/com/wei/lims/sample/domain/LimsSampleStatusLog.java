package com.wei.lims.sample.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 样品状态变更日志对象 lims_sample_status_log
 */
public class LimsSampleStatusLog {
    private static final long serialVersionUID = 1L;

    private Long logId;
    private Long sampleId;
    private String fromStatus;
    private String toStatus;
    private String changedBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date changedTime;
    private String reason;

    public Long getLogId() { return logId; }
    public void setLogId(Long logId) { this.logId = logId; }
    public Long getSampleId() { return sampleId; }
    public void setSampleId(Long sampleId) { this.sampleId = sampleId; }
    public String getFromStatus() { return fromStatus; }
    public void setFromStatus(String fromStatus) { this.fromStatus = fromStatus; }
    public String getToStatus() { return toStatus; }
    public void setToStatus(String toStatus) { this.toStatus = toStatus; }
    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }
    public Date getChangedTime() { return changedTime; }
    public void setChangedTime(Date changedTime) { this.changedTime = changedTime; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
