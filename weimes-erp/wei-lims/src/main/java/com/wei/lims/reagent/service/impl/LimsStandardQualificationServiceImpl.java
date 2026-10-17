package com.wei.lims.reagent.service.impl;

import java.util.List;
import com.wei.lims.reagent.domain.LimsStandardQualification;
import com.wei.lims.reagent.mapper.LimsStandardQualificationMapper;
import com.wei.lims.reagent.service.ILimsStandardQualificationService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsStandardQualificationServiceImpl implements ILimsStandardQualificationService {

    @Autowired
    private LimsStandardQualificationMapper limsStandardQualificationMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsStandardQualification selectByQualId(Long qualId) {
        return limsStandardQualificationMapper.selectByQualId(qualId);
    }

    @Override
    public List<LimsStandardQualification> selectByStandardId(Long standardId) {
        return limsStandardQualificationMapper.selectByStandardId(standardId);
    }

    @Override
    public List<LimsStandardQualification> selectLimsStandardQualificationList(LimsStandardQualification qualification) {
        return limsStandardQualificationMapper.selectLimsStandardQualificationList(qualification);
    }

    @Override
    @Transactional
    public int insertLimsStandardQualification(LimsStandardQualification qualification) {
        int rows = limsStandardQualificationMapper.insertLimsStandardQualification(qualification);
        limsAuditTrailService.recordFieldChanges("lims_standard_qualification", qualification.getQualId(),
                "INSERT", null, qualification, "新建标准品标定记录");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsStandardQualification(LimsStandardQualification qualification) {
        LimsStandardQualification oldQual = limsStandardQualificationMapper.selectByQualId(qualification.getQualId());
        int rows = limsStandardQualificationMapper.updateLimsStandardQualification(qualification);
        limsAuditTrailService.recordFieldChanges("lims_standard_qualification", qualification.getQualId(),
                "UPDATE", oldQual, qualification, "更新标准品标定记录");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsStandardQualificationByIds(Long[] qualIds) {
        for (Long qualId : qualIds) {
            LimsStandardQualification oldQual = limsStandardQualificationMapper.selectByQualId(qualId);
            limsAuditTrailService.recordFieldChanges("lims_standard_qualification", qualId,
                    "DELETE", oldQual, null, "删除标准品标定记录");
        }
        return limsStandardQualificationMapper.deleteLimsStandardQualificationByIds(qualIds);
    }
}
