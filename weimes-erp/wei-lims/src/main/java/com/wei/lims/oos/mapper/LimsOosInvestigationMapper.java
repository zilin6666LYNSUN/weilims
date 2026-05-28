package com.wei.lims.oos.mapper;

import java.util.List;
import com.wei.lims.oos.domain.LimsOosInvestigation;

public interface LimsOosInvestigationMapper {
    public LimsOosInvestigation selectByInvestigationId(Long investigationId);
    public List<LimsOosInvestigation> selectLimsOosInvestigationList(LimsOosInvestigation investigation);
    public int insertLimsOosInvestigation(LimsOosInvestigation investigation);
    public int updateLimsOosInvestigation(LimsOosInvestigation investigation);
    public int deleteLimsOosInvestigationByIds(Long[] investigationIds);
}
