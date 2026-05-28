package com.wei.lims.reagent.service;

import java.util.List;
import com.wei.lims.reagent.domain.LimsReferenceStandard;

public interface ILimsReferenceStandardService {
    public LimsReferenceStandard selectByStandardId(Long standardId);
    public List<LimsReferenceStandard> selectLimsReferenceStandardList(LimsReferenceStandard standard);
    public int insertLimsReferenceStandard(LimsReferenceStandard standard);
    public int updateLimsReferenceStandard(LimsReferenceStandard standard);
    public int deleteLimsReferenceStandardByIds(Long[] standardIds);
}
