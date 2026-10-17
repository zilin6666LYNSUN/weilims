package com.wei.lims.training.domain;

import java.util.Date;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 培训计划对象 lims_training_plan
 */
public class LimsTrainingPlan {
    private static final long serialVersionUID = 1L;

    private Long planId;

    private String planCode;

    private String planName;

    private Integer planYear;

    private String trainingType;

    private String status;

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }

    public String getPlanCode() { return planCode; }
    public void setPlanCode(String planCode) { this.planCode = planCode; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public Integer getPlanYear() { return planYear; }
    public void setPlanYear(Integer planYear) { this.planYear = planYear; }

    public String getTrainingType() { return trainingType; }
    public void setTrainingType(String trainingType) { this.trainingType = trainingType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}