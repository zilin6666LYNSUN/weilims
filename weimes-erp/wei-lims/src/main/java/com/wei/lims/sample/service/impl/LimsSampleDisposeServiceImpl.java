package com.wei.lims.sample.service.impl;

import java.util.Date;
import java.util.List;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import com.wei.lims.sample.domain.LimsSampleDispose;
import com.wei.lims.sample.mapper.LimsSampleDisposeMapper;
import com.wei.lims.sample.service.ILimsSampleDisposeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsSampleDisposeServiceImpl implements ILimsSampleDisposeService {

    @Autowired
    private LimsSampleDisposeMapper limsSampleDisposeMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsSampleDispose selectByDisposeId(Long disposeId) {
        return limsSampleDisposeMapper.selectByDisposeId(disposeId);
    }

    @Override
    public List<LimsSampleDispose> selectBySampleId(Long sampleId) {
        return limsSampleDisposeMapper.selectBySampleId(sampleId);
    }

    @Override
    public List<LimsSampleDispose> selectLimsSampleDisposeList(LimsSampleDispose dispose) {
        return limsSampleDisposeMapper.selectLimsSampleDisposeList(dispose);
    }

    @Override
    @Transactional
    public int insertLimsSampleDispose(LimsSampleDispose dispose) {
        int rows = limsSampleDisposeMapper.insertLimsSampleDispose(dispose);
        limsAuditTrailService.recordFieldChanges("lims_sample_dispose", dispose.getDisposeId(),
                "INSERT", null, dispose, "新增样品处置记录");
        return rows;
    }

    @Override
    @Transactional
    public int approveDispose(Long disposeId, Long approvedBy) {
        LimsSampleDispose dispose = limsSampleDisposeMapper.selectByDisposeId(disposeId);
        if (dispose == null) return 0;
        dispose.setApprovedBy(approvedBy);
        int rows = limsSampleDisposeMapper.updateLimsSampleDispose(dispose);
        limsAuditTrailService.recordFieldChanges("lims_sample_dispose", disposeId,
                "UPDATE", null, dispose, "审批处置记录");
        return rows;
    }

    @Override
    public int deleteByDisposeIds(Long[] disposeIds) {
        return limsSampleDisposeMapper.deleteByDisposeIds(disposeIds);
    }
}
