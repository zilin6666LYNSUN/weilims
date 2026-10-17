package com.wei.lims.training.service;

import java.util.List;
import com.wei.lims.training.domain.LimsTrainingPlan;

public interface ILimsTrainingPlanService {
    public LimsTrainingPlan selectLimsTrainingPlanByPlanId(Long planId);
    public List<LimsTrainingPlan> selectLimsTrainingPlanList(LimsTrainingPlan LimsTrainingPlan);
    public int insertLimsTrainingPlan(LimsTrainingPlan limstrainingplan);
    public int updateLimsTrainingPlan(LimsTrainingPlan limstrainingplan);
    public int deleteLimsTrainingPlanByPlanId(Long[] planIds);
}