package com.wei.lims.reagent.service;

import java.util.List;
import com.wei.lims.reagent.domain.LimsStandardQualification;

public interface ILimsStandardQualificationService {
    public LimsStandardQualification selectByQualId(Long qualId);
    public List<LimsStandardQualification> selectByStandardId(Long standardId);
    public List<LimsStandardQualification> selectLimsStandardQualificationList(LimsStandardQualification qualification);
    public int insertLimsStandardQualification(LimsStandardQualification qualification);
    public int updateLimsStandardQualification(LimsStandardQualification qualification);
    public int deleteLimsStandardQualificationByIds(Long[] qualIds);
}
