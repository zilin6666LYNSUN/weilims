package com.wei.lims.supplier.service;

import java.util.List;
import com.wei.lims.supplier.domain.LimsSupplier;

public interface ILimsSupplierService {
    public LimsSupplier selectLimsSupplierByVendorId(Long vendorId);
    public List<LimsSupplier> selectLimsSupplierList(LimsSupplier LimsSupplier);
    public int insertLimsSupplier(LimsSupplier limssupplier);
    public int updateLimsSupplier(LimsSupplier limssupplier);
    public int deleteLimsSupplierByVendorId(Long[] vendorIds);
}