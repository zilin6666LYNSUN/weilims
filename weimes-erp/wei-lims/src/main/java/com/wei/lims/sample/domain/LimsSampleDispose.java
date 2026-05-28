package com.wei.lims.sample.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 样品处置记录对象 lims_sample_dispose
 */
public class LimsSampleDispose {
    private static final long serialVersionUID = 1L;

    private Long disposeId;
    private Long sampleId;
    private String disposeType;
    private BigDecimal disposeQuantity;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date disposeDate;
    private Long disposedBy;
    private Long approvedBy;
    private String reason;

    public Long getDisposeId() { return disposeId; }
    public void setDisposeId(Long disposeId) { this.disposeId = disposeId; }
    public Long getSampleId() { return sampleId; }
    public void setSampleId(Long sampleId) { this.sampleId = sampleId; }
    public String getDisposeType() { return disposeType; }
    public void setDisposeType(String disposeType) { this.disposeType = disposeType; }
    public BigDecimal getDisposeQuantity() { return disposeQuantity; }
    public void setDisposeQuantity(BigDecimal disposeQuantity) { this.disposeQuantity = disposeQuantity; }
    public Date getDisposeDate() { return disposeDate; }
    public void setDisposeDate(Date disposeDate) { this.disposeDate = disposeDate; }
    public Long getDisposedBy() { return disposedBy; }
    public void setDisposedBy(Long disposedBy) { this.disposedBy = disposedBy; }
    public Long getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
