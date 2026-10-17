package com.wei.lims.change.mapper;

import java.util.List;
import com.wei.lims.change.domain.LimsChangeImpact;

public interface LimsChangeImpactMapper {
    public LimsChangeImpact selectByImpactId(Long impactId);
    public List<LimsChangeImpact> selectByChangeId(Long changeId);
    public List<LimsChangeImpact> selectLimsChangeImpactList(LimsChangeImpact impact);
    public int insertLimsChangeImpact(LimsChangeImpact impact);
    public int updateLimsChangeImpact(LimsChangeImpact impact);
    public int deleteLimsChangeImpactByIds(Long[] impactIds);
}
