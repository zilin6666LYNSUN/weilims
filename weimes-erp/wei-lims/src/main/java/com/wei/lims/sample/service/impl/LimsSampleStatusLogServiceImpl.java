package com.wei.lims.sample.service.impl;

import java.util.Date;
import java.util.List;
import com.wei.common.utils.SecurityUtils;
import com.wei.lims.sample.domain.LimsSampleStatusLog;
import com.wei.lims.sample.mapper.LimsSampleStatusLogMapper;
import com.wei.lims.sample.service.ILimsSampleStatusLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LimsSampleStatusLogServiceImpl implements ILimsSampleStatusLogService {

    @Autowired
    private LimsSampleStatusLogMapper limsSampleStatusLogMapper;

    @Override
    public List<LimsSampleStatusLog> selectBySampleId(Long sampleId) {
        return limsSampleStatusLogMapper.selectBySampleId(sampleId);
    }

    @Override
    public List<LimsSampleStatusLog> selectLimsSampleStatusLogList(LimsSampleStatusLog log) {
        return limsSampleStatusLogMapper.selectLimsSampleStatusLogList(log);
    }

    @Override
    public int insertLimsSampleStatusLog(LimsSampleStatusLog log) {
        log.setChangedTime(new Date());
        try {
            log.setChangedBy(SecurityUtils.getUsername());
        } catch (Exception e) {
            log.setChangedBy("SYSTEM");
        }
        return limsSampleStatusLogMapper.insertLimsSampleStatusLog(log);
    }
}
