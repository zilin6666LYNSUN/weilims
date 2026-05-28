package com.wei.lims.esign.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wei.common.core.domain.BaseEntity;

/**
 * 电子签名对象 lims_electronic_signature
 * 满足 21 CFR Part 11 要求
 */
public class LimsElectronicSignature extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long signatureId;

    /** 签名对象类型(如: LimsTestOrder, LimsSample, LimsDeviation等) */
    private String recordType;

    /** 签名对象ID */
    private Long recordId;

    /** 签名人ID */
    private Long signerId;

    /** 签名人姓名 */
    private String signerName;

    /** 签名含义: DRAFTED/REVIEWED/APPROVED/REJECTED/VERIFIED */
    private String signatureMeaning;

    /** 签名时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date signatureTime;

    /** 签名理由(合规必填) */
    private String reason;

    /** 签名是否有效 */
    private Integer isValid;

    /** IP地址 */
    private String ipAddress;

    /** 会话ID */
    private String sessionId;

    public Long getSignatureId() { return signatureId; }
    public void setSignatureId(Long signatureId) { this.signatureId = signatureId; }
    public String getRecordType() { return recordType; }
    public void setRecordType(String recordType) { this.recordType = recordType; }
    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }
    public Long getSignerId() { return signerId; }
    public void setSignerId(Long signerId) { this.signerId = signerId; }
    public String getSignerName() { return signerName; }
    public void setSignerName(String signerName) { this.signerName = signerName; }
    public String getSignatureMeaning() { return signatureMeaning; }
    public void setSignatureMeaning(String signatureMeaning) { this.signatureMeaning = signatureMeaning; }
    public Date getSignatureTime() { return signatureTime; }
    public void setSignatureTime(Date signatureTime) { this.signatureTime = signatureTime; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Integer getIsValid() { return isValid; }
    public void setIsValid(Integer isValid) { this.isValid = isValid; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
}
