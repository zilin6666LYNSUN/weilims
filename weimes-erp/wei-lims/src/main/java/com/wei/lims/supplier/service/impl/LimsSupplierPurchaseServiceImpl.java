package com.wei.lims.supplier.service.impl;

import java.util.List;
import com.wei.lims.supplier.domain.LimsSupplierPurchase;
import com.wei.lims.supplier.mapper.LimsSupplierPurchaseMapper;
import com.wei.lims.supplier.service.ILimsSupplierPurchaseService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsSupplierPurchaseServiceImpl implements ILimsSupplierPurchaseService {

    @Autowired
    private LimsSupplierPurchaseMapper limssupplierpurchaseMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsSupplierPurchase selectLimsSupplierPurchaseByPurchaseId(Long purchaseId) {
        return limssupplierpurchaseMapper.selectLimsSupplierPurchaseByPurchaseId(purchaseId);
    }

    @Override
    public List<LimsSupplierPurchase> selectLimsSupplierPurchaseList(LimsSupplierPurchase limssupplierpurchase) {
        return limssupplierpurchaseMapper.selectLimsSupplierPurchaseList(limssupplierpurchase);
    }

    @Override
    @Transactional
    public int insertLimsSupplierPurchase(LimsSupplierPurchase limssupplierpurchase) {
        int rows = limssupplierpurchaseMapper.insertLimsSupplierPurchase(limssupplierpurchase);
        limsAuditTrailService.recordFieldChanges("lims_supplier_purchase", limssupplierpurchase.getPurchaseId(), "INSERT", null, limssupplierpurchase, "新建供应商采购记录");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsSupplierPurchase(LimsSupplierPurchase limssupplierpurchase) {
        LimsSupplierPurchase old = limssupplierpurchaseMapper.selectLimsSupplierPurchaseByPurchaseId(limssupplierpurchase.getPurchaseId());
        int rows = limssupplierpurchaseMapper.updateLimsSupplierPurchase(limssupplierpurchase);
        limsAuditTrailService.recordFieldChanges("lims_supplier_purchase", limssupplierpurchase.getPurchaseId(), "UPDATE", old, limssupplierpurchase, "更新供应商采购记录");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsSupplierPurchaseByPurchaseId(Long[] purchaseIds) {
        for (Long purchaseId : purchaseIds) {
            LimsSupplierPurchase old = limssupplierpurchaseMapper.selectLimsSupplierPurchaseByPurchaseId(purchaseId);
            limsAuditTrailService.recordFieldChanges("lims_supplier_purchase", purchaseId, "DELETE", old, null, "删除供应商采购记录");
        }
        return limssupplierpurchaseMapper.deleteLimsSupplierPurchaseByPurchaseId(purchaseIds);
    }
}