package com.wei.lims.entrust.service.impl;

import java.util.List;
import com.wei.lims.entrust.domain.LimsEntrust;
import com.wei.lims.entrust.mapper.LimsEntrustMapper;
import com.wei.lims.entrust.service.ILimsEntrustService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import com.wei.lims.sample.domain.LimsSample;
import com.wei.lims.sample.mapper.LimsSampleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsEntrustServiceImpl implements ILimsEntrustService {

    @Autowired
    private LimsEntrustMapper limsentrustMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Autowired
    private LimsSampleMapper limsSampleMapper;

    @Override
    public LimsEntrust selectLimsEntrustByEntrustId(Long entrustId) {
        return limsentrustMapper.selectLimsEntrustByEntrustId(entrustId);
    }

    @Override
    public List<LimsEntrust> selectLimsEntrustList(LimsEntrust limsentrust) {
        return limsentrustMapper.selectLimsEntrustList(limsentrust);
    }

    @Override
    @Transactional
    public int insertLimsEntrust(LimsEntrust limsentrust) {
        int rows = limsentrustMapper.insertLimsEntrust(limsentrust);
        limsAuditTrailService.recordFieldChanges("lims_entrust", limsentrust.getEntrustId(), "INSERT", null, limsentrust, "新建委托单");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsEntrust(LimsEntrust limsentrust) {
        LimsEntrust old = limsentrustMapper.selectLimsEntrustByEntrustId(limsentrust.getEntrustId());
        int rows = limsentrustMapper.updateLimsEntrust(limsentrust);
        limsAuditTrailService.recordFieldChanges("lims_entrust", limsentrust.getEntrustId(), "UPDATE", old, limsentrust, "更新委托单");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsEntrustByEntrustId(Long[] entrustIds) {
        for (Long entrustId : entrustIds) {
            LimsEntrust old = limsentrustMapper.selectLimsEntrustByEntrustId(entrustId);
            limsAuditTrailService.recordFieldChanges("lims_entrust", entrustId, "DELETE", old, null, "删除委托单");
        }
        return limsentrustMapper.deleteLimsEntrustByEntrustId(entrustIds);
    }

    @Override
    @Transactional
    public int submitLimsEntrust(Long entrustId) {
        LimsEntrust entrust = limsentrustMapper.selectLimsEntrustByEntrustId(entrustId);
        if (entrust == null) {
            return 0;
        }
        if ("DRAFT".equals(entrust.getStatus())) {
            entrust.setStatus("REVIEW");
            limsentrustMapper.updateLimsEntrust(entrust);
            limsAuditTrailService.recordFieldChanges("lims_entrust", entrustId, "UPDATE", null, entrust, "提交受理评审");
        }
        return 1;
    }

    @Override
    @Transactional
    public int reviewLimsEntrust(LimsEntrust entrust) {
        LimsEntrust old = limsentrustMapper.selectLimsEntrustByEntrustId(entrust.getEntrustId());
        if (old == null) {
            return 0;
        }
        old.setStatus(entrust.getStatus());
        old.setReviewBy(entrust.getReviewBy());
        old.setReviewTime(entrust.getReviewTime());
        old.setReviewComment(entrust.getReviewComment());
        int rows = limsentrustMapper.updateLimsEntrust(old);
        limsAuditTrailService.recordFieldChanges("lims_entrust", old.getEntrustId(), "UPDATE", null, old, "受理评审(" + old.getStatus() + ")");
        return rows;
    }

    @Override
    @Transactional
    public Long registerSample(Long entrustId) {
        LimsEntrust entrust = limsentrustMapper.selectLimsEntrustByEntrustId(entrustId);
        if (entrust == null || !"APPROVED".equals(entrust.getStatus())) {
            return -1L;
        }
        if (entrust.getSampleId() != null) {
            return entrust.getSampleId();
        }
        LimsSample sample = new LimsSample();
        sample.setSampleCode("EN-" + entrust.getEntrustCode());
        sample.setSampleName(entrust.getSampleName());
        sample.setSampleType("成品");
        sample.setSampleSource("客户委托");
        sample.setBatchNo(entrust.getBatchNo());
        sample.setSpecification(entrust.getSpecification());
        sample.setQuantityReceived(entrust.getQuantity());
        sample.setQuantityRemaining(entrust.getQuantity());
        sample.setUnitOfMeasure(entrust.getUnitOfMeasure());
        sample.setStatus("待接收");
        limsSampleMapper.insertLimsSample(sample);
        entrust.setSampleId(sample.getSampleId());
        entrust.setStatus("REGISTERED");
        limsentrustMapper.updateLimsEntrust(entrust);
        limsAuditTrailService.recordFieldChanges("lims_entrust", entrustId, "UPDATE", null, entrust, "受理通过登记样品");
        return sample.getSampleId();
    }
}