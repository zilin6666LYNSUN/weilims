package com.wei.lims.oos.service;

import java.util.List;
import com.wei.lims.oos.domain.LimsOosInvestigation;

public interface ILimsOosInvestigationService {
    public LimsOosInvestigation selectByInvestigationId(Long investigationId);
    public List<LimsOosInvestigation> selectLimsOosInvestigationList(LimsOosInvestigation investigation);
    public int insertLimsOosInvestigation(LimsOosInvestigation investigation);
    public int updateLimsOosInvestigation(LimsOosInvestigation investigation);
    public int deleteLimsOosInvestigationByIds(Long[] investigationIds);
    public int submitInvestigation(Long investigationId);
    public int reviewInvestigation(Long investigationId);
    public int approveInvestigation(Long investigationId);
}
