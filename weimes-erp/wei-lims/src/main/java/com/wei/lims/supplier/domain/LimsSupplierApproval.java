package com.wei.lims.supplier.domain;

import java.util.Date;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 合格供应商名录对象 lims_supplier_approval
 */
public class LimsSupplierApproval {
    private static final long serialVersionUID = 1L;

    private Long approvalId;

    private Long vendorId;

    private Long productId;

    private String approvalType;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date approvedDate;

    private Long approvedBy;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expireDate;

    private String status;

    public Long getApprovalId() { return approvalId; }
    public void setApprovalId(Long approvalId) { this.approvalId = approvalId; }

    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getApprovalType() { return approvalType; }
    public void setApprovalType(String approvalType) { this.approvalType = approvalType; }

    public Date getApprovedDate() { return approvedDate; }
    public void setApprovedDate(Date approvedDate) { this.approvedDate = approvedDate; }

    public Long getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }

    public Date getExpireDate() { return expireDate; }
    public void setExpireDate(Date expireDate) { this.expireDate = expireDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}