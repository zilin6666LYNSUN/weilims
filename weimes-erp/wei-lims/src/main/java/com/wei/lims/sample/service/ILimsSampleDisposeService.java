package com.wei.lims.sample.service;

import java.util.List;
import com.wei.lims.sample.domain.LimsSampleDispose;

public interface ILimsSampleDisposeService {

    public LimsSampleDispose selectByDisposeId(Long disposeId);
    public List<LimsSampleDispose> selectBySampleId(Long sampleId);
    public List<LimsSampleDispose> selectLimsSampleDisposeList(LimsSampleDispose dispose);
    public int insertLimsSampleDispose(LimsSampleDispose dispose);
    public int approveDispose(Long disposeId, Long approvedBy);
    public int deleteByDisposeIds(Long[] disposeIds);
}
