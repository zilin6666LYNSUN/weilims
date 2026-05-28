package com.wei.lims.sample.service.impl;

import java.util.List;
import com.wei.common.constant.UserConstants;
import com.wei.common.utils.DateUtils;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import com.wei.lims.sample.domain.LimsSample;
import com.wei.lims.sample.mapper.LimsSampleMapper;
import com.wei.lims.sample.service.ILimsSampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 样品管理Service实现
 */
@Service
public class LimsSampleServiceImpl implements ILimsSampleService {

    @Autowired
    private LimsSampleMapper limsSampleMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsSample selectLimsSampleBySampleId(Long sampleId) {
        return limsSampleMapper.selectLimsSampleBySampleId(sampleId);
    }

    @Override
    public List<LimsSample> selectLimsSampleList(LimsSample limsSample) {
        return limsSampleMapper.selectLimsSampleList(limsSample);
    }

    @Override
    @Transactional
    public int insertLimsSample(LimsSample limsSample) {
        limsSample.setCreateTime(DateUtils.getNowDate());
        int rows = limsSampleMapper.insertLimsSample(limsSample);
        limsAuditTrailService.recordFieldChanges("lims_sample", limsSample.getSampleId(),
                "INSERT", null, limsSample, "新建样品");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsSample(LimsSample limsSample) {
        LimsSample oldSample = limsSampleMapper.selectLimsSampleBySampleId(limsSample.getSampleId());
        limsSample.setUpdateTime(DateUtils.getNowDate());
        int rows = limsSampleMapper.updateLimsSample(limsSample);
        limsAuditTrailService.recordFieldChanges("lims_sample", limsSample.getSampleId(),
                "UPDATE", oldSample, limsSample, "更新样品");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsSampleBySampleIds(Long[] sampleIds) {
        for (Long sampleId : sampleIds) {
            limsAuditTrailService.recordFieldChanges("lims_sample", sampleId,
                    "DELETE", null, null, "删除样品");
        }
        return limsSampleMapper.deleteLimsSampleBySampleIds(sampleIds);
    }

    @Override
    public String checkSampleCodeUnique(LimsSample limsSample) {
        LimsSample existing = limsSampleMapper.selectLimsSampleBySampleCode(limsSample.getSampleCode());
        if (existing != null && !existing.getSampleId().equals(limsSample.getSampleId())) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    @Transactional
    public int receiveSample(Long sampleId) {
        LimsSample sample = limsSampleMapper.selectLimsSampleBySampleId(sampleId);
        if (sample == null) {
            return 0;
        }
        String oldStatus = sample.getStatus();
        sample.setStatus("已接收");
        sample.setReceiveDate(DateUtils.getNowDate());
        int rows = limsSampleMapper.updateLimsSample(sample);
        limsAuditTrailService.recordFieldChanges("lims_sample", sampleId,
                "UPDATE", null, sample, "样品接收,状态从" + oldStatus + "变更为已接收");
        return rows;
    }

    @Override
    @Transactional
    public int changeSampleStatus(Long sampleId, String toStatus, String reason) {
        LimsSample sample = limsSampleMapper.selectLimsSampleBySampleId(sampleId);
        if (sample == null) {
            return 0;
        }
        String oldStatus = sample.getStatus();
        sample.setStatus(toStatus);
        int rows = limsSampleMapper.updateLimsSample(sample);
        limsAuditTrailService.recordFieldChanges("lims_sample", sampleId,
                "UPDATE", null, sample, reason != null ? reason : "状态从" + oldStatus + "变更为" + toStatus);
        return rows;
    }
}
