package com.wei.lims.supplier.service.impl;

import java.util.List;
import com.wei.lims.supplier.domain.LimsSupplierAudit;
import com.wei.lims.supplier.mapper.LimsSupplierAuditMapper;
import com.wei.lims.supplier.service.ILimsSupplierAuditService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import com.wei.lims.supplier.domain.LimsSupplier;
import com.wei.lims.supplier.mapper.LimsSupplierMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsSupplierAuditServiceImpl implements ILimsSupplierAuditService {

    @Autowired
    private LimsSupplierAuditMapper limssupplierauditMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Autowired
    private LimsSupplierMapper limsSupplierMapper;

    @Override
    public LimsSupplierAudit selectLimsSupplierAuditByAuditId(Long auditId) {
        return limssupplierauditMapper.selectLimsSupplierAuditByAuditId(auditId);
    }

    @Override
    public List<LimsSupplierAudit> selectLimsSupplierAuditList(LimsSupplierAudit limssupplieraudit) {
        return limssupplierauditMapper.selectLimsSupplierAuditList(limssupplieraudit);
    }

    @Override
    @Transactional
    public int insertLimsSupplierAudit(LimsSupplierAudit limssupplieraudit) {
        int rows = limssupplierauditMapper.insertLimsSupplierAudit(limssupplieraudit);
        limsAuditTrailService.recordFieldChanges("lims_supplier_audit", limssupplieraudit.getAuditId(), "INSERT", null, limssupplieraudit, "新建供应商审计");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsSupplierAudit(LimsSupplierAudit limssupplieraudit) {
        LimsSupplierAudit old = limssupplierauditMapper.selectLimsSupplierAuditByAuditId(limssupplieraudit.getAuditId());
        int rows = limssupplierauditMapper.updateLimsSupplierAudit(limssupplieraudit);
        limsAuditTrailService.recordFieldChanges("lims_supplier_audit", limssupplieraudit.getAuditId(), "UPDATE", old, limssupplieraudit, "更新供应商审计");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsSupplierAuditByAuditId(Long[] auditIds) {
        for (Long auditId : auditIds) {
            LimsSupplierAudit old = limssupplierauditMapper.selectLimsSupplierAuditByAuditId(auditId);
            limsAuditTrailService.recordFieldChanges("lims_supplier_audit", auditId, "DELETE", old, null, "删除供应商审计");
        }
        return limssupplierauditMapper.deleteLimsSupplierAuditByAuditId(auditIds);
    }
}