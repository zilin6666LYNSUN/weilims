package com.wei.lims.deviation.service;

import java.util.List;
import com.wei.lims.deviation.domain.LimsDeviation;

public interface ILimsDeviationService {
    public LimsDeviation selectByDeviationId(Long deviationId);
    public List<LimsDeviation> selectLimsDeviationList(LimsDeviation deviation);
    public int insertLimsDeviation(LimsDeviation deviation);
    public int updateLimsDeviation(LimsDeviation deviation);
    public int deleteLimsDeviationByIds(Long[] deviationIds);
    public int submitDeviation(Long deviationId);
    public int approveDeviation(Long deviationId);
    public int closeDeviation(Long deviationId);
}
