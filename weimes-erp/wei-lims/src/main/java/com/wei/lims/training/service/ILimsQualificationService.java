package com.wei.lims.training.service;

import java.util.List;
import com.wei.lims.training.domain.LimsQualification;

public interface ILimsQualificationService {
    public LimsQualification selectLimsQualificationByQualificationId(Long qualificationId);
    public List<LimsQualification> selectLimsQualificationList(LimsQualification LimsQualification);
    public int insertLimsQualification(LimsQualification limsqualification);
    public int updateLimsQualification(LimsQualification limsqualification);
    public int deleteLimsQualificationByQualificationId(Long[] qualificationIds);

    public List<LimsQualification> selectExpiringQualifications();
}