package com.wei.lims.supplier.mapper;

import java.util.List;
import com.wei.lims.supplier.domain.LimsSupplier;

public interface LimsSupplierMapper {
    public LimsSupplier selectLimsSupplierByVendorId(Long vendorId);
    public List<LimsSupplier> selectLimsSupplierList(LimsSupplier LimsSupplier);
    public int insertLimsSupplier(LimsSupplier limssupplier);
    public int updateLimsSupplier(LimsSupplier limssupplier);
    public int deleteLimsSupplierByVendorId(Long[] vendorIds);
}