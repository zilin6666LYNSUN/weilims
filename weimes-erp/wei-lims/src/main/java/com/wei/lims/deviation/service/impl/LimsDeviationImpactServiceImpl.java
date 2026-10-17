package com.wei.lims.deviation.service.impl;

import java.util.List;
import com.wei.lims.deviation.domain.LimsDeviationImpact;
import com.wei.lims.deviation.mapper.LimsDeviationImpactMapper;
import com.wei.lims.deviation.service.ILimsDeviationImpactService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsDeviationImpactServiceImpl implements ILimsDeviationImpactService {

    @Autowired
    private LimsDeviationImpactMapper limsDeviationImpactMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsDeviationImpact selectByImpactId(Long impactId) {
        return limsDeviationImpactMapper.selectByImpactId(impactId);
    }

    @Override
    public List<LimsDeviationImpact> selectByDeviationId(Long deviationId) {
        return limsDeviationImpactMapper.selectByDeviationId(deviationId);
    }

    @Override
    public List<LimsDeviationImpact> selectLimsDeviationImpactList(LimsDeviationImpact impact) {
        return limsDeviationImpactMapper.selectLimsDeviationImpactList(impact);
    }

    @Override
    @Transactional
    public int insertLimsDeviationImpact(LimsDeviationImpact impact) {
        int rows = limsDeviationImpactMapper.insertLimsDeviationImpact(impact);
        limsAuditTrailService.recordFieldChanges("lims_deviation_impact", impact.getImpactId(),
                "INSERT", null, impact, "新建偏差影响评估");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsDeviationImpact(LimsDeviationImpact impact) {
        LimsDeviationImpact oldImpact = limsDeviationImpactMapper.selectByImpactId(impact.getImpactId());
        int rows = limsDeviationImpactMapper.updateLimsDeviationImpact(impact);
        limsAuditTrailService.recordFieldChanges("lims_deviation_impact", impact.getImpactId(),
                "UPDATE", oldImpact, impact, "更新偏差影响评估");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsDeviationImpactByIds(Long[] impactIds) {
        for (Long impactId : impactIds) {
            LimsDeviationImpact oldImpact = limsDeviationImpactMapper.selectByImpactId(impactId);
            limsAuditTrailService.recordFieldChanges("lims_deviation_impact", impactId,
                    "DELETE", oldImpact, null, "删除偏差影响评估");
        }
        return limsDeviationImpactMapper.deleteLimsDeviationImpactByIds(impactIds);
    }
}
