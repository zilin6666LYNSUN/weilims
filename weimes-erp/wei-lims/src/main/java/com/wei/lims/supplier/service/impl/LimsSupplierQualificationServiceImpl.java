package com.wei.lims.supplier.service.impl;

import java.util.List;
import com.wei.lims.supplier.domain.LimsSupplierQualification;
import com.wei.lims.supplier.mapper.LimsSupplierQualificationMapper;
import com.wei.lims.supplier.service.ILimsSupplierQualificationService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsSupplierQualificationServiceImpl implements ILimsSupplierQualificationService {

    @Autowired
    private LimsSupplierQualificationMapper limssupplierqualificationMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsSupplierQualification selectLimsSupplierQualificationByQualificationId(Long qualificationId) {
        return limssupplierqualificationMapper.selectLimsSupplierQualificationByQualificationId(qualificationId);
    }

    @Override
    public List<LimsSupplierQualification> selectLimsSupplierQualificationList(LimsSupplierQualification limssupplierqualification) {
        return limssupplierqualificationMapper.selectLimsSupplierQualificationList(limssupplierqualification);
    }

    @Override
    @Transactional
    public int insertLimsSupplierQualification(LimsSupplierQualification limssupplierqualification) {
        int rows = limssupplierqualificationMapper.insertLimsSupplierQualification(limssupplierqualification);
        limsAuditTrailService.recordFieldChanges("lims_supplier_qualification", limssupplierqualification.getQualificationId(), "INSERT", null, limssupplierqualification, "新建供应商资质");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsSupplierQualification(LimsSupplierQualification limssupplierqualification) {
        LimsSupplierQualification old = limssupplierqualificationMapper.selectLimsSupplierQualificationByQualificationId(limssupplierqualification.getQualificationId());
        int rows = limssupplierqualificationMapper.updateLimsSupplierQualification(limssupplierqualification);
        limsAuditTrailService.recordFieldChanges("lims_supplier_qualification", limssupplierqualification.getQualificationId(), "UPDATE", old, limssupplierqualification, "更新供应商资质");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsSupplierQualificationByQualificationId(Long[] qualificationIds) {
        for (Long qualificationId : qualificationIds) {
            LimsSupplierQualification old = limssupplierqualificationMapper.selectLimsSupplierQualificationByQualificationId(qualificationId);
            limsAuditTrailService.recordFieldChanges("lims_supplier_qualification", qualificationId, "DELETE", old, null, "删除供应商资质");
        }
        return limssupplierqualificationMapper.deleteLimsSupplierQualificationByQualificationId(qualificationIds);
    }
}