package com.wei.lims.supplier.mapper;

import java.util.List;
import com.wei.lims.supplier.domain.LimsSupplierPurchase;

public interface LimsSupplierPurchaseMapper {
    public LimsSupplierPurchase selectLimsSupplierPurchaseByPurchaseId(Long purchaseId);
    public List<LimsSupplierPurchase> selectLimsSupplierPurchaseList(LimsSupplierPurchase LimsSupplierPurchase);
    public int insertLimsSupplierPurchase(LimsSupplierPurchase limssupplierpurchase);
    public int updateLimsSupplierPurchase(LimsSupplierPurchase limssupplierpurchase);
    public int deleteLimsSupplierPurchaseByPurchaseId(Long[] purchaseIds);
}