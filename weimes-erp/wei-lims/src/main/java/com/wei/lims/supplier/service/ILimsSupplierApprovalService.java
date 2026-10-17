package com.wei.lims.supplier.service;

import java.util.List;
import com.wei.lims.supplier.domain.LimsSupplierApproval;

public interface ILimsSupplierApprovalService {
    public LimsSupplierApproval selectLimsSupplierApprovalByApprovalId(Long approvalId);
    public List<LimsSupplierApproval> selectLimsSupplierApprovalList(LimsSupplierApproval LimsSupplierApproval);
    public int insertLimsSupplierApproval(LimsSupplierApproval limssupplierapproval);
    public int updateLimsSupplierApproval(LimsSupplierApproval limssupplierapproval);
    public int deleteLimsSupplierApprovalByApprovalId(Long[] approvalIds);
}