package com.wei.lims.stability.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

public class LimsStabilityTest {
    private static final long serialVersionUID = 1L;
    private Long stabilityTestId;
    private Long protocolId;
    private String testPoint;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date plannedDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date actualDate;
    private Long testOrderId;
    private String testResultSummary;
    private Boolean isOot;
    private String status;

    public Long getStabilityTestId() { return stabilityTestId; }
    public void setStabilityTestId(Long stabilityTestId) { this.stabilityTestId = stabilityTestId; }
    public Long getProtocolId() { return protocolId; }
    public void setProtocolId(Long protocolId) { this.protocolId = protocolId; }
    public String getTestPoint() { return testPoint; }
    public void setTestPoint(String testPoint) { this.testPoint = testPoint; }
    public Date getPlannedDate() { return plannedDate; }
    public void setPlannedDate(Date plannedDate) { this.plannedDate = plannedDate; }
    public Date getActualDate() { return actualDate; }
    public void setActualDate(Date actualDate) { this.actualDate = actualDate; }
    public Long getTestOrderId() { return testOrderId; }
    public void setTestOrderId(Long testOrderId) { this.testOrderId = testOrderId; }
    public String getTestResultSummary() { return testResultSummary; }
    public void setTestResultSummary(String testResultSummary) { this.testResultSummary = testResultSummary; }
    public Boolean getIsOot() { return isOot; }
    public void setIsOot(Boolean isOot) { this.isOot = isOot; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
