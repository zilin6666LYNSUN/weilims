package com.wei.lims.supplier.service.impl;

import java.util.List;
import com.wei.lims.supplier.domain.LimsSupplier;
import com.wei.lims.supplier.mapper.LimsSupplierMapper;
import com.wei.lims.supplier.service.ILimsSupplierService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsSupplierServiceImpl implements ILimsSupplierService {

    @Autowired
    private LimsSupplierMapper limssupplierMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsSupplier selectLimsSupplierByVendorId(Long vendorId) {
        return limssupplierMapper.selectLimsSupplierByVendorId(vendorId);
    }

    @Override
    public List<LimsSupplier> selectLimsSupplierList(LimsSupplier limssupplier) {
        return limssupplierMapper.selectLimsSupplierList(limssupplier);
    }

    @Override
    @Transactional
    public int insertLimsSupplier(LimsSupplier limssupplier) {
        int rows = limssupplierMapper.insertLimsSupplier(limssupplier);
        limsAuditTrailService.recordFieldChanges("lims_supplier", limssupplier.getVendorId(), "INSERT", null, limssupplier, "新建供应商档案");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsSupplier(LimsSupplier limssupplier) {
        LimsSupplier old = limssupplierMapper.selectLimsSupplierByVendorId(limssupplier.getVendorId());
        int rows = limssupplierMapper.updateLimsSupplier(limssupplier);
        limsAuditTrailService.recordFieldChanges("lims_supplier", limssupplier.getVendorId(), "UPDATE", old, limssupplier, "更新供应商档案");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsSupplierByVendorId(Long[] vendorIds) {
        for (Long vendorId : vendorIds) {
            LimsSupplier old = limssupplierMapper.selectLimsSupplierByVendorId(vendorId);
            limsAuditTrailService.recordFieldChanges("lims_supplier", vendorId, "DELETE", old, null, "删除供应商档案");
        }
        return limssupplierMapper.deleteLimsSupplierByVendorId(vendorIds);
    }
}