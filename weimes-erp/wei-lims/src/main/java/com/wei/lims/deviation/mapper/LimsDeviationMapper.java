package com.wei.lims.deviation.mapper;

import java.util.List;
import com.wei.lims.deviation.domain.LimsDeviation;

public interface LimsDeviationMapper {
    public LimsDeviation selectByDeviationId(Long deviationId);
    public List<LimsDeviation> selectLimsDeviationList(LimsDeviation deviation);
    public int insertLimsDeviation(LimsDeviation deviation);
    public int updateLimsDeviation(LimsDeviation deviation);
    public int deleteLimsDeviationByIds(Long[] deviationIds);
}
