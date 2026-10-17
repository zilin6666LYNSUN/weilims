package com.wei.lims.deviation.mapper;

import java.util.List;
import com.wei.lims.deviation.domain.LimsDeviationImpact;

public interface LimsDeviationImpactMapper {
    public LimsDeviationImpact selectByImpactId(Long impactId);
    public List<LimsDeviationImpact> selectByDeviationId(Long deviationId);
    public List<LimsDeviationImpact> selectLimsDeviationImpactList(LimsDeviationImpact impact);
    public int insertLimsDeviationImpact(LimsDeviationImpact impact);
    public int updateLimsDeviationImpact(LimsDeviationImpact impact);
    public int deleteLimsDeviationImpactByIds(Long[] impactIds);
}
