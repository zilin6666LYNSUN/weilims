package com.wei.lims.change.service;

import java.util.List;
import com.wei.lims.change.domain.LimsChangeImpact;

public interface ILimsChangeImpactService {
    public LimsChangeImpact selectByImpactId(Long impactId);
    public List<LimsChangeImpact> selectByChangeId(Long changeId);
    public List<LimsChangeImpact> selectLimsChangeImpactList(LimsChangeImpact impact);
    public int insertLimsChangeImpact(LimsChangeImpact impact);
    public int updateLimsChangeImpact(LimsChangeImpact impact);
    public int deleteLimsChangeImpactByIds(Long[] impactIds);
}
