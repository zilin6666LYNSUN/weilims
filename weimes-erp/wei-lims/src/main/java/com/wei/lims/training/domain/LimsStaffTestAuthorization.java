package com.wei.lims.training.domain;

import java.util.Date;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 人员检测项授权对象 lims_staff_test_authorization
 */
public class LimsStaffTestAuthorization {
    private static final long serialVersionUID = 1L;

    private Long authId;

    private Long staffId;

    private Long testItemId;

    private String testItemName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date authorizedDate;

    private Long authorizedBy;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expireDate;

    private String status;

    public Long getAuthId() { return authId; }
    public void setAuthId(Long authId) { this.authId = authId; }

    public Long getStaffId() { return staffId; }
    public void setStaffId(Long staffId) { this.staffId = staffId; }

    public Long getTestItemId() { return testItemId; }
    public void setTestItemId(Long testItemId) { this.testItemId = testItemId; }

    public String getTestItemName() { return testItemName; }
    public void setTestItemName(String testItemName) { this.testItemName = testItemName; }

    public Date getAuthorizedDate() { return authorizedDate; }
    public void setAuthorizedDate(Date authorizedDate) { this.authorizedDate = authorizedDate; }

    public Long getAuthorizedBy() { return authorizedBy; }
    public void setAuthorizedBy(Long authorizedBy) { this.authorizedBy = authorizedBy; }

    public Date getExpireDate() { return expireDate; }
    public void setExpireDate(Date expireDate) { this.expireDate = expireDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}