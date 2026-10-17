package com.wei.lims.training.service.impl;

import java.util.List;
import com.wei.lims.training.domain.LimsQualification;
import com.wei.lims.training.mapper.LimsQualificationMapper;
import com.wei.lims.training.service.ILimsQualificationService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsQualificationServiceImpl implements ILimsQualificationService {

    @Autowired
    private LimsQualificationMapper limsqualificationMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsQualification selectLimsQualificationByQualificationId(Long qualificationId) {
        return limsqualificationMapper.selectLimsQualificationByQualificationId(qualificationId);
    }

    @Override
    public List<LimsQualification> selectLimsQualificationList(LimsQualification limsqualification) {
        return limsqualificationMapper.selectLimsQualificationList(limsqualification);
    }

    @Override
    @Transactional
    public int insertLimsQualification(LimsQualification limsqualification) {
        int rows = limsqualificationMapper.insertLimsQualification(limsqualification);
        limsAuditTrailService.recordFieldChanges("lims_qualification", limsqualification.getQualificationId(), "INSERT", null, limsqualification, "新建人员资质");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsQualification(LimsQualification limsqualification) {
        LimsQualification old = limsqualificationMapper.selectLimsQualificationByQualificationId(limsqualification.getQualificationId());
        int rows = limsqualificationMapper.updateLimsQualification(limsqualification);
        limsAuditTrailService.recordFieldChanges("lims_qualification", limsqualification.getQualificationId(), "UPDATE", old, limsqualification, "更新人员资质");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsQualificationByQualificationId(Long[] qualificationIds) {
        for (Long qualificationId : qualificationIds) {
            LimsQualification old = limsqualificationMapper.selectLimsQualificationByQualificationId(qualificationId);
            limsAuditTrailService.recordFieldChanges("lims_qualification", qualificationId, "DELETE", old, null, "删除人员资质");
        }
        return limsqualificationMapper.deleteLimsQualificationByQualificationId(qualificationIds);
    }

    @Override
    public List<LimsQualification> selectExpiringQualifications() {
        return limsqualificationMapper.selectExpiringQualifications();
    }
}