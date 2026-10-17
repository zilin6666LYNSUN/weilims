package com.wei.lims.deviation.service.impl;

import java.util.List;
import com.wei.common.utils.DateUtils;
import com.wei.lims.deviation.domain.LimsDeviation;
import com.wei.lims.deviation.domain.LimsDeviationImpact;
import com.wei.lims.deviation.mapper.LimsDeviationMapper;
import com.wei.lims.deviation.mapper.LimsDeviationImpactMapper;
import com.wei.lims.deviation.service.ILimsDeviationService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsDeviationServiceImpl implements ILimsDeviationService {

    @Autowired
    private LimsDeviationMapper limsDeviationMapper;

    @Autowired
    private LimsDeviationImpactMapper limsDeviationImpactMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsDeviation selectByDeviationId(Long deviationId) {
        return limsDeviationMapper.selectByDeviationId(deviationId);
    }

    @Override
    public List<LimsDeviation> selectLimsDeviationList(LimsDeviation deviation) {
        return limsDeviationMapper.selectLimsDeviationList(deviation);
    }

    @Override
    @Transactional
    public int insertLimsDeviation(LimsDeviation deviation) {
        deviation.setCreateTime(DateUtils.getNowDate());
        int rows = limsDeviationMapper.insertLimsDeviation(deviation);
        limsAuditTrailService.recordFieldChanges("lims_deviation", deviation.getDeviationId(),
                "INSERT", null, deviation, "新建偏差");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsDeviation(LimsDeviation deviation) {
        LimsDeviation oldDeviation = limsDeviationMapper.selectByDeviationId(deviation.getDeviationId());
        deviation.setUpdateTime(DateUtils.getNowDate());
        int rows = limsDeviationMapper.updateLimsDeviation(deviation);
        limsAuditTrailService.recordFieldChanges("lims_deviation", deviation.getDeviationId(),
                "UPDATE", oldDeviation, deviation, "更新偏差");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsDeviationByIds(Long[] deviationIds) {
        for (Long deviationId : deviationIds) {
            limsAuditTrailService.recordFieldChanges("lims_deviation", deviationId,
                    "DELETE", null, null, "删除偏差");
        }
        return limsDeviationMapper.deleteLimsDeviationByIds(deviationIds);
    }

    @Override
    @Transactional
    public int submitDeviation(Long deviationId) {
        LimsDeviation oldD = limsDeviationMapper.selectByDeviationId(deviationId);
        LimsDeviation newD = limsDeviationMapper.selectByDeviationId(deviationId);
        newD.setStatus("SUBMITTED");
        int rows = limsDeviationMapper.updateLimsDeviation(newD);
        limsAuditTrailService.recordFieldChanges("lims_deviation", deviationId,
                "UPDATE", oldD, newD, "提交偏差");
        return rows;
    }

    @Override
    @Transactional
    public int approveDeviation(Long deviationId) {
        LimsDeviation oldD = limsDeviationMapper.selectByDeviationId(deviationId);
        LimsDeviation newD = limsDeviationMapper.selectByDeviationId(deviationId);
        newD.setStatus("APPROVED");
        int rows = limsDeviationMapper.updateLimsDeviation(newD);
        limsAuditTrailService.recordFieldChanges("lims_deviation", deviationId,
                "UPDATE", oldD, newD, "审批偏差");
        // 自动触发偏差影响评估任务（待质量人员完善评估结论）
        LimsDeviationImpact impact = new LimsDeviationImpact();
        impact.setDeviationId(deviationId);
        impact.setImpactArea("产品质量");
        impact.setImpactLevel("待评估");
        impact.setAssessedDate(DateUtils.getNowDate());
        limsDeviationImpactMapper.insertLimsDeviationImpact(impact);
        limsAuditTrailService.recordFieldChanges("lims_deviation_impact", impact.getImpactId(),
                "INSERT", null, impact, "审批偏差自动触发影响评估");
        return rows;
    }

    @Override
    @Transactional
    public int closeDeviation(Long deviationId) {
        LimsDeviation oldD = limsDeviationMapper.selectByDeviationId(deviationId);
        LimsDeviation newD = limsDeviationMapper.selectByDeviationId(deviationId);
        newD.setStatus("CLOSED");
        newD.setCloseDate(DateUtils.getNowDate());
        int rows = limsDeviationMapper.updateLimsDeviation(newD);
        limsAuditTrailService.recordFieldChanges("lims_deviation", deviationId,
                "UPDATE", oldD, newD, "关闭偏差");
        return rows;
    }
}
