package com.wei.lims.sample.service.impl;

import java.util.Date;
import java.util.List;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import com.wei.lims.sample.domain.LimsRetainSample;
import com.wei.lims.sample.mapper.LimsRetainSampleMapper;
import com.wei.lims.sample.service.ILimsRetainSampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsRetainSampleServiceImpl implements ILimsRetainSampleService {

    @Autowired
    private LimsRetainSampleMapper limsRetainSampleMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsRetainSample selectByRetainId(Long retainId) {
        return limsRetainSampleMapper.selectByRetainId(retainId);
    }

    @Override
    public List<LimsRetainSample> selectBySampleId(Long sampleId) {
        return limsRetainSampleMapper.selectBySampleId(sampleId);
    }

    @Override
    public List<LimsRetainSample> selectLimsRetainSampleList(LimsRetainSample retain) {
        return limsRetainSampleMapper.selectLimsRetainSampleList(retain);
    }

    @Override
    @Transactional
    public int insertLimsRetainSample(LimsRetainSample retain) {
        retain.setStatus("RETAINED");
        int rows = limsRetainSampleMapper.insertLimsRetainSample(retain);
        limsAuditTrailService.recordFieldChanges("lims_retain_sample", retain.getRetainId(),
                "INSERT", null, retain, "新增留样记录");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsRetainSample(LimsRetainSample retain) {
        int rows = limsRetainSampleMapper.updateLimsRetainSample(retain);
        limsAuditTrailService.recordFieldChanges("lims_retain_sample", retain.getRetainId(),
                "UPDATE", null, retain, "更新留样记录");
        return rows;
    }

    @Override
    @Transactional
    public int disposeRetainSample(Long retainId, String disposeType, String disposeApprover) {
        LimsRetainSample retain = limsRetainSampleMapper.selectByRetainId(retainId);
        if (retain == null) return 0;
        retain.setStatus("DISPOSED");
        retain.setDisposeType(disposeType);
        retain.setDisposeDate(new Date());
        retain.setDisposeApprover(disposeApprover);
        int rows = limsRetainSampleMapper.updateLimsRetainSample(retain);
        limsAuditTrailService.recordFieldChanges("lims_retain_sample", retainId,
                "UPDATE", null, retain, "处置留样: " + disposeType);
        return rows;
    }

    @Override
    public int deleteByRetainIds(Long[] retainIds) {
        return limsRetainSampleMapper.deleteByRetainIds(retainIds);
    }
}
