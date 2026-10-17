package com.wei.lims.change.service.impl;

import java.util.List;
import com.wei.lims.change.domain.LimsChangeImpact;
import com.wei.lims.change.mapper.LimsChangeImpactMapper;
import com.wei.lims.change.service.ILimsChangeImpactService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsChangeImpactServiceImpl implements ILimsChangeImpactService {

    @Autowired
    private LimsChangeImpactMapper limsChangeImpactMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsChangeImpact selectByImpactId(Long impactId) {
        return limsChangeImpactMapper.selectByImpactId(impactId);
    }

    @Override
    public List<LimsChangeImpact> selectByChangeId(Long changeId) {
        return limsChangeImpactMapper.selectByChangeId(changeId);
    }

    @Override
    public List<LimsChangeImpact> selectLimsChangeImpactList(LimsChangeImpact impact) {
        return limsChangeImpactMapper.selectLimsChangeImpactList(impact);
    }

    @Override
    @Transactional
    public int insertLimsChangeImpact(LimsChangeImpact impact) {
        int rows = limsChangeImpactMapper.insertLimsChangeImpact(impact);
        limsAuditTrailService.recordFieldChanges("lims_change_impact", impact.getImpactId(),
                "INSERT", null, impact, "新建变更影响评估");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsChangeImpact(LimsChangeImpact impact) {
        LimsChangeImpact oldImpact = limsChangeImpactMapper.selectByImpactId(impact.getImpactId());
        int rows = limsChangeImpactMapper.updateLimsChangeImpact(impact);
        limsAuditTrailService.recordFieldChanges("lims_change_impact", impact.getImpactId(),
                "UPDATE", oldImpact, impact, "更新变更影响评估");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsChangeImpactByIds(Long[] impactIds) {
        for (Long impactId : impactIds) {
            LimsChangeImpact oldImpact = limsChangeImpactMapper.selectByImpactId(impactId);
            limsAuditTrailService.recordFieldChanges("lims_change_impact", impactId,
                    "DELETE", oldImpact, null, "删除变更影响评估");
        }
        return limsChangeImpactMapper.deleteLimsChangeImpactByIds(impactIds);
    }
}
