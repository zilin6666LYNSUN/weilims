package com.wei.lims.sample.service;

import java.util.List;
import com.wei.lims.sample.domain.LimsSampleStatusLog;

/**
 * 样品状态日志Service接口
 */
public interface ILimsSampleStatusLogService {

    public List<LimsSampleStatusLog> selectBySampleId(Long sampleId);

    public List<LimsSampleStatusLog> selectLimsSampleStatusLogList(LimsSampleStatusLog log);

    public int insertLimsSampleStatusLog(LimsSampleStatusLog log);
}
