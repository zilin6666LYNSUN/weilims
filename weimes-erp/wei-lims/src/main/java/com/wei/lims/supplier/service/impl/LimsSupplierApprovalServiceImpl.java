package com.wei.lims.supplier.service.impl;

import java.util.List;
import com.wei.lims.supplier.domain.LimsSupplierApproval;
import com.wei.lims.supplier.mapper.LimsSupplierApprovalMapper;
import com.wei.lims.supplier.service.ILimsSupplierApprovalService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import com.wei.lims.supplier.domain.LimsSupplier;
import com.wei.lims.supplier.mapper.LimsSupplierMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsSupplierApprovalServiceImpl implements ILimsSupplierApprovalService {

    @Autowired
    private LimsSupplierApprovalMapper limssupplierapprovalMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Autowired
    private LimsSupplierMapper limsSupplierMapper;

    @Override
    public LimsSupplierApproval selectLimsSupplierApprovalByApprovalId(Long approvalId) {
        return limssupplierapprovalMapper.selectLimsSupplierApprovalByApprovalId(approvalId);
    }

    @Override
    public List<LimsSupplierApproval> selectLimsSupplierApprovalList(LimsSupplierApproval limssupplierapproval) {
        return limssupplierapprovalMapper.selectLimsSupplierApprovalList(limssupplierapproval);
    }

    @Override
    @Transactional
    public int insertLimsSupplierApproval(LimsSupplierApproval limssupplierapproval) {
        int rows = limssupplierapprovalMapper.insertLimsSupplierApproval(limssupplierapproval);
        limsAuditTrailService.recordFieldChanges("lims_supplier_approval", limssupplierapproval.getApprovalId(), "INSERT", null, limssupplierapproval, "新建合格供应商名录");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsSupplierApproval(LimsSupplierApproval limssupplierapproval) {
        LimsSupplierApproval old = limssupplierapprovalMapper.selectLimsSupplierApprovalByApprovalId(limssupplierapproval.getApprovalId());
        int rows = limssupplierapprovalMapper.updateLimsSupplierApproval(limssupplierapproval);
        limsAuditTrailService.recordFieldChanges("lims_supplier_approval", limssupplierapproval.getApprovalId(), "UPDATE", old, limssupplierapproval, "更新合格供应商名录");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsSupplierApprovalByApprovalId(Long[] approvalIds) {
        for (Long approvalId : approvalIds) {
            LimsSupplierApproval old = limssupplierapprovalMapper.selectLimsSupplierApprovalByApprovalId(approvalId);
            limsAuditTrailService.recordFieldChanges("lims_supplier_approval", approvalId, "DELETE", old, null, "删除合格供应商名录");
        }
        return limssupplierapprovalMapper.deleteLimsSupplierApprovalByApprovalId(approvalIds);
    }
}