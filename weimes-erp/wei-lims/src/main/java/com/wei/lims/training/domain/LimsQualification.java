package com.wei.lims.training.domain;

import java.util.Date;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 人员资质对象 lims_qualification
 */
public class LimsQualification {
    private static final long serialVersionUID = 1L;

    private Long qualificationId;

    private Long staffId;

    private String qualificationType;

    private String qualificationName;

    private String qualificationScope;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date grantedDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expireDate;

    private Long grantedBy;

    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date requalificationDate;

    public Long getQualificationId() { return qualificationId; }
    public void setQualificationId(Long qualificationId) { this.qualificationId = qualificationId; }

    public Long getStaffId() { return staffId; }
    public void setStaffId(Long staffId) { this.staffId = staffId; }

    public String getQualificationType() { return qualificationType; }
    public void setQualificationType(String qualificationType) { this.qualificationType = qualificationType; }

    public String getQualificationName() { return qualificationName; }
    public void setQualificationName(String qualificationName) { this.qualificationName = qualificationName; }

    public String getQualificationScope() { return qualificationScope; }
    public void setQualificationScope(String qualificationScope) { this.qualificationScope = qualificationScope; }

    public Date getGrantedDate() { return grantedDate; }
    public void setGrantedDate(Date grantedDate) { this.grantedDate = grantedDate; }

    public Date getExpireDate() { return expireDate; }
    public void setExpireDate(Date expireDate) { this.expireDate = expireDate; }

    public Long getGrantedBy() { return grantedBy; }
    public void setGrantedBy(Long grantedBy) { this.grantedBy = grantedBy; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getRequalificationDate() { return requalificationDate; }
    public void setRequalificationDate(Date requalificationDate) { this.requalificationDate = requalificationDate; }

}