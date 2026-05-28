package com.wei.lims.instrument.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 仪器预约对象 lims_instrument_reservation
 */
public class LimsInstrumentReservation {
    private static final long serialVersionUID = 1L;

    private Long reservationId;
    private Long instrumentId;
    private Long testOrderId;
    private Long testerId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date reservedDate;
    @JsonFormat(pattern = "HH:mm:ss")
    private Date startTime;
    @JsonFormat(pattern = "HH:mm:ss")
    private Date endTime;
    private String purpose;
    private String status;

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }
    public Long getInstrumentId() { return instrumentId; }
    public void setInstrumentId(Long instrumentId) { this.instrumentId = instrumentId; }
    public Long getTestOrderId() { return testOrderId; }
    public void setTestOrderId(Long testOrderId) { this.testOrderId = testOrderId; }
    public Long getTesterId() { return testerId; }
    public void setTesterId(Long testerId) { this.testerId = testerId; }
    public Date getReservedDate() { return reservedDate; }
    public void setReservedDate(Date reservedDate) { this.reservedDate = reservedDate; }
    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
