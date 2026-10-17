package com.wei.lims.supplier.domain;

import java.util.Date;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 供应商审计对象 lims_supplier_audit
 */
public class LimsSupplierAudit {
    private static final long serialVersionUID = 1L;

    private Long auditId;

    private Long vendorId;

    private String auditCode;

    private String auditType;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date auditDate;

    private String auditorIds;

    private String auditScope;

    private String auditFindings;

    private String auditClassification;

    private Integer capaRequired;

    private String capaIds;

    private BigDecimal auditScore;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date nextAuditDate;

    private String status;

    public Long getAuditId() { return auditId; }
    public void setAuditId(Long auditId) { this.auditId = auditId; }

    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }

    public String getAuditCode() { return auditCode; }
    public void setAuditCode(String auditCode) { this.auditCode = auditCode; }

    public String getAuditType() { return auditType; }
    public void setAuditType(String auditType) { this.auditType = auditType; }

    public Date getAuditDate() { return auditDate; }
    public void setAuditDate(Date auditDate) { this.auditDate = auditDate; }

    public String getAuditorIds() { return auditorIds; }
    public void setAuditorIds(String auditorIds) { this.auditorIds = auditorIds; }

    public String getAuditScope() { return auditScope; }
    public void setAuditScope(String auditScope) { this.auditScope = auditScope; }

    public String getAuditFindings() { return auditFindings; }
    public void setAuditFindings(String auditFindings) { this.auditFindings = auditFindings; }

    public String getAuditClassification() { return auditClassification; }
    public void setAuditClassification(String auditClassification) { this.auditClassification = auditClassification; }

    public Integer getCapaRequired() { return capaRequired; }
    public void setCapaRequired(Integer capaRequired) { this.capaRequired = capaRequired; }

    public String getCapaIds() { return capaIds; }
    public void setCapaIds(String capaIds) { this.capaIds = capaIds; }

    public BigDecimal getAuditScore() { return auditScore; }
    public void setAuditScore(BigDecimal auditScore) { this.auditScore = auditScore; }

    public Date getNextAuditDate() { return nextAuditDate; }
    public void setNextAuditDate(Date nextAuditDate) { this.nextAuditDate = nextAuditDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}