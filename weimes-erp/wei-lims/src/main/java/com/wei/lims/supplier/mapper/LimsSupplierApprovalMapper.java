package com.wei.lims.supplier.mapper;

import java.util.List;
import com.wei.lims.supplier.domain.LimsSupplierApproval;

public interface LimsSupplierApprovalMapper {
    public LimsSupplierApproval selectLimsSupplierApprovalByApprovalId(Long approvalId);
    public List<LimsSupplierApproval> selectLimsSupplierApprovalList(LimsSupplierApproval LimsSupplierApproval);
    public int insertLimsSupplierApproval(LimsSupplierApproval limssupplierapproval);
    public int updateLimsSupplierApproval(LimsSupplierApproval limssupplierapproval);
    public int deleteLimsSupplierApprovalByApprovalId(Long[] approvalIds);
}