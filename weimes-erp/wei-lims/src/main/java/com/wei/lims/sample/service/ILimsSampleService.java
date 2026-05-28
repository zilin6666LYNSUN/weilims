package com.wei.lims.sample.service;

import java.util.List;
import com.wei.lims.sample.domain.LimsSample;

/**
 * 样品管理Service接口
 */
public interface ILimsSampleService {

    public LimsSample selectLimsSampleBySampleId(Long sampleId);

    public List<LimsSample> selectLimsSampleList(LimsSample limsSample);

    public int insertLimsSample(LimsSample limsSample);

    public int updateLimsSample(LimsSample limsSample);

    public int deleteLimsSampleBySampleIds(Long[] sampleIds);

    public String checkSampleCodeUnique(LimsSample limsSample);

    /**
     * 样品接收
     */
    public int receiveSample(Long sampleId);

    /**
     * 样品状态变更
     */
    public int changeSampleStatus(Long sampleId, String toStatus, String reason);
}
