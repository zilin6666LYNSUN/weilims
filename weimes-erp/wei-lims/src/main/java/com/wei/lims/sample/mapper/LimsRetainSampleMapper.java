package com.wei.lims.sample.mapper;

import java.util.List;
import com.wei.lims.sample.domain.LimsRetainSample;

/**
 * 留样管理Mapper接口
 */
public interface LimsRetainSampleMapper {

    public LimsRetainSample selectByRetainId(Long retainId);

    public List<LimsRetainSample> selectBySampleId(Long sampleId);

    public List<LimsRetainSample> selectLimsRetainSampleList(LimsRetainSample retain);

    public int insertLimsRetainSample(LimsRetainSample retain);

    public int updateLimsRetainSample(LimsRetainSample retain);

    public int deleteByRetainIds(Long[] retainIds);
}
