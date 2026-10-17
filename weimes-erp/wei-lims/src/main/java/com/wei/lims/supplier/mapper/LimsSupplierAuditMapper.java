package com.wei.lims.supplier.mapper;

import java.util.List;
import com.wei.lims.supplier.domain.LimsSupplierAudit;

public interface LimsSupplierAuditMapper {
    public LimsSupplierAudit selectLimsSupplierAuditByAuditId(Long auditId);
    public List<LimsSupplierAudit> selectLimsSupplierAuditList(LimsSupplierAudit LimsSupplierAudit);
    public int insertLimsSupplierAudit(LimsSupplierAudit limssupplieraudit);
    public int updateLimsSupplierAudit(LimsSupplierAudit limssupplieraudit);
    public int deleteLimsSupplierAuditByAuditId(Long[] auditIds);
}