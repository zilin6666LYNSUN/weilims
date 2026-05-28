package com.wei.lims.sample.mapper;

import java.util.List;
import com.wei.lims.sample.domain.LimsSampleStatusLog;

/**
 * 样品状态日志Mapper接口
 */
public interface LimsSampleStatusLogMapper {

    public List<LimsSampleStatusLog> selectBySampleId(Long sampleId);

    public LimsSampleStatusLog selectByLogId(Long logId);

    public List<LimsSampleStatusLog> selectLimsSampleStatusLogList(LimsSampleStatusLog log);

    public int insertLimsSampleStatusLog(LimsSampleStatusLog log);
}
