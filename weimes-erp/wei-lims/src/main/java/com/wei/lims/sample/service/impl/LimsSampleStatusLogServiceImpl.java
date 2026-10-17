package com.wei.lims.sample.service.impl;

import java.util.Date;
import java.util.List;
import com.wei.common.utils.SecurityUtils;
import com.wei.lims.sample.domain.LimsSampleStatusLog;
import com.wei.lims.sample.mapper.LimsSampleStatusLogMapper;
import com.wei.lims.sample.service.ILimsSampleStatusLogService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsSampleStatusLogServiceImpl implements ILimsSampleStatusLogService {

    @Autowired
    private LimsSampleStatusLogMapper limsSampleStatusLogMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public List<LimsSampleStatusLog> selectBySampleId(Long sampleId) {
        return limsSampleStatusLogMapper.selectBySampleId(sampleId);
    }

    @Override
    public List<LimsSampleStatusLog> selectLimsSampleStatusLogList(LimsSampleStatusLog log) {
        return limsSampleStatusLogMapper.selectLimsSampleStatusLogList(log);
    }

    @Override
    @Transactional
    public int insertLimsSampleStatusLog(LimsSampleStatusLog log) {
        log.setChangedTime(new Date());
        try {
            log.setChangedBy(SecurityUtils.getUsername());
        } catch (Exception e) {
            log.setChangedBy("SYSTEM");
        }
        int rows = limsSampleStatusLogMapper.insertLimsSampleStatusLog(log);
        limsAuditTrailService.recordFieldChanges("lims_sample_status_log", log.getLogId(),
                "INSERT", null, log, "记录样品状态变更");
        return rows;
    }
}
