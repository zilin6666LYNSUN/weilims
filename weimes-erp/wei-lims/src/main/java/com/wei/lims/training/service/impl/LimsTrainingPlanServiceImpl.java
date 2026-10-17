package com.wei.lims.training.service.impl;

import java.util.List;
import com.wei.lims.training.domain.LimsTrainingPlan;
import com.wei.lims.training.mapper.LimsTrainingPlanMapper;
import com.wei.lims.training.service.ILimsTrainingPlanService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsTrainingPlanServiceImpl implements ILimsTrainingPlanService {

    @Autowired
    private LimsTrainingPlanMapper limstrainingplanMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsTrainingPlan selectLimsTrainingPlanByPlanId(Long planId) {
        return limstrainingplanMapper.selectLimsTrainingPlanByPlanId(planId);
    }

    @Override
    public List<LimsTrainingPlan> selectLimsTrainingPlanList(LimsTrainingPlan limstrainingplan) {
        return limstrainingplanMapper.selectLimsTrainingPlanList(limstrainingplan);
    }

    @Override
    @Transactional
    public int insertLimsTrainingPlan(LimsTrainingPlan limstrainingplan) {
        int rows = limstrainingplanMapper.insertLimsTrainingPlan(limstrainingplan);
        limsAuditTrailService.recordFieldChanges("lims_training_plan", limstrainingplan.getPlanId(), "INSERT", null, limstrainingplan, "新建培训计划");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsTrainingPlan(LimsTrainingPlan limstrainingplan) {
        LimsTrainingPlan old = limstrainingplanMapper.selectLimsTrainingPlanByPlanId(limstrainingplan.getPlanId());
        int rows = limstrainingplanMapper.updateLimsTrainingPlan(limstrainingplan);
        limsAuditTrailService.recordFieldChanges("lims_training_plan", limstrainingplan.getPlanId(), "UPDATE", old, limstrainingplan, "更新培训计划");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsTrainingPlanByPlanId(Long[] planIds) {
        for (Long planId : planIds) {
            LimsTrainingPlan old = limstrainingplanMapper.selectLimsTrainingPlanByPlanId(planId);
            limsAuditTrailService.recordFieldChanges("lims_training_plan", planId, "DELETE", old, null, "删除培训计划");
        }
        return limstrainingplanMapper.deleteLimsTrainingPlanByPlanId(planIds);
    }
}