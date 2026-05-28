package com.wei.lims.sample.mapper;

import java.util.List;
import com.wei.lims.sample.domain.LimsSample;

/**
 * 样品管理Mapper接口
 */
public interface LimsSampleMapper {

    public LimsSample selectLimsSampleBySampleId(Long sampleId);

    public LimsSample selectLimsSampleBySampleCode(String sampleCode);

    public List<LimsSample> selectLimsSampleList(LimsSample limsSample);

    public int insertLimsSample(LimsSample limsSample);

    public int updateLimsSample(LimsSample limsSample);

    public int deleteLimsSampleBySampleIds(Long[] sampleIds);

    public int deleteLimsSampleBySampleId(Long sampleId);

    public String checkSampleCodeUnique(LimsSample limsSample);
}
