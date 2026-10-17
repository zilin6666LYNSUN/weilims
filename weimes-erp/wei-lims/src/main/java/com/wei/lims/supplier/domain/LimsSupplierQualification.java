package com.wei.lims.supplier.domain;

import java.util.Date;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 供应商资质对象 lims_supplier_qualification
 */
public class LimsSupplierQualification {
    private static final long serialVersionUID = 1L;

    private Long qualificationId;

    private Long vendorId;

    private String qualificationType;

    private String certificateNo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date issueDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expireDate;

    private String certificateFile;

    private String status;

    public Long getQualificationId() { return qualificationId; }
    public void setQualificationId(Long qualificationId) { this.qualificationId = qualificationId; }

    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }

    public String getQualificationType() { return qualificationType; }
    public void setQualificationType(String qualificationType) { this.qualificationType = qualificationType; }

    public String getCertificateNo() { return certificateNo; }
    public void setCertificateNo(String certificateNo) { this.certificateNo = certificateNo; }

    public Date getIssueDate() { return issueDate; }
    public void setIssueDate(Date issueDate) { this.issueDate = issueDate; }

    public Date getExpireDate() { return expireDate; }
    public void setExpireDate(Date expireDate) { this.expireDate = expireDate; }

    public String getCertificateFile() { return certificateFile; }
    public void setCertificateFile(String certificateFile) { this.certificateFile = certificateFile; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}