package com.wei.lims.sample.mapper;

import java.util.List;
import com.wei.lims.sample.domain.LimsSampleDispose;

/**
 * 样品处置Mapper接口
 */
public interface LimsSampleDisposeMapper {

    public LimsSampleDispose selectByDisposeId(Long disposeId);

    public List<LimsSampleDispose> selectBySampleId(Long sampleId);

    public List<LimsSampleDispose> selectLimsSampleDisposeList(LimsSampleDispose dispose);

    public int insertLimsSampleDispose(LimsSampleDispose dispose);

    public int updateLimsSampleDispose(LimsSampleDispose dispose);

    public int deleteByDisposeIds(Long[] disposeIds);
}
