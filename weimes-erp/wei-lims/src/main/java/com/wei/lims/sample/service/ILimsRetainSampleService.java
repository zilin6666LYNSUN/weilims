package com.wei.lims.sample.service;

import java.util.List;
import com.wei.lims.sample.domain.LimsRetainSample;

public interface ILimsRetainSampleService {

    public LimsRetainSample selectByRetainId(Long retainId);
    public List<LimsRetainSample> selectBySampleId(Long sampleId);
    public List<LimsRetainSample> selectLimsRetainSampleList(LimsRetainSample retain);
    public int insertLimsRetainSample(LimsRetainSample retain);
    public int updateLimsRetainSample(LimsRetainSample retain);
    public int disposeRetainSample(Long retainId, String disposeType, String disposeApprover);
    public int deleteByRetainIds(Long[] retainIds);
}
