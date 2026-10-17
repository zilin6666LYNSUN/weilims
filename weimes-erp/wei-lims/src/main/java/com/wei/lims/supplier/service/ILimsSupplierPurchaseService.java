package com.wei.lims.supplier.service;

import java.util.List;
import com.wei.lims.supplier.domain.LimsSupplierPurchase;

public interface ILimsSupplierPurchaseService {
    public LimsSupplierPurchase selectLimsSupplierPurchaseByPurchaseId(Long purchaseId);
    public List<LimsSupplierPurchase> selectLimsSupplierPurchaseList(LimsSupplierPurchase LimsSupplierPurchase);
    public int insertLimsSupplierPurchase(LimsSupplierPurchase limssupplierpurchase);
    public int updateLimsSupplierPurchase(LimsSupplierPurchase limssupplierpurchase);
    public int deleteLimsSupplierPurchaseByPurchaseId(Long[] purchaseIds);
}