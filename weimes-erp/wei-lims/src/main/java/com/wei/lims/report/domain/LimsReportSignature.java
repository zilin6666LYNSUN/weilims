package com.wei.lims.report.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wei.common.core.domain.BaseEntity;

/**
 * 报告签章记录对象 lims_report_signature
 * 21 CFR Part 11合规：记录每次签章操作
 */
public class LimsReportSignature extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long signatureId;
    private Long reportId;

    /** 签章类型: PREPARE/REVIEW/APPROVE/RELEASE */
    private String signatureType;

    private Long signerId;
    private String signerName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date signatureTime;

    private String signatureMeaning;
    private Integer isValid;
    private String ipAddress;
    private String reason;

    public Long getSignatureId() { return signatureId; }
    public void setSignatureId(Long signatureId) { this.signatureId = signatureId; }
    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }
    public String getSignatureType() { return signatureType; }
    public void setSignatureType(String signatureType) { this.signatureType = signatureType; }
    public Long getSignerId() { return signerId; }
    public void setSignerId(Long signerId) { this.signerId = signerId; }
    public String getSignerName() { return signerName; }
    public void setSignerName(String signerName) { this.signerName = signerName; }
    public Date getSignatureTime() { return signatureTime; }
    public void setSignatureTime(Date signatureTime) { this.signatureTime = signatureTime; }
    public String getSignatureMeaning() { return signatureMeaning; }
    public void setSignatureMeaning(String signatureMeaning) { this.signatureMeaning = signatureMeaning; }
    public Integer getIsValid() { return isValid; }
    public void setIsValid(Integer isValid) { this.isValid = isValid; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
