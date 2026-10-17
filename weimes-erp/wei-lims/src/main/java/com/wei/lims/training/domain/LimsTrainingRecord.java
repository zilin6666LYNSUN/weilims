package com.wei.lims.training.domain;

import java.util.Date;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 培训记录对象 lims_training_record
 */
public class LimsTrainingRecord {
    private static final long serialVersionUID = 1L;

    private Long trainingId;

    private Long planId;

    private String trainingTopic;

    private String trainingType;

    private String trainingMethod;

    private Long trainerId;

    private String trainerName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date trainingDate;

    private BigDecimal trainingHours;

    private String trainingContent;

    private String status;

    public Long getTrainingId() { return trainingId; }
    public void setTrainingId(Long trainingId) { this.trainingId = trainingId; }

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }

    public String getTrainingTopic() { return trainingTopic; }
    public void setTrainingTopic(String trainingTopic) { this.trainingTopic = trainingTopic; }

    public String getTrainingType() { return trainingType; }
    public void setTrainingType(String trainingType) { this.trainingType = trainingType; }

    public String getTrainingMethod() { return trainingMethod; }
    public void setTrainingMethod(String trainingMethod) { this.trainingMethod = trainingMethod; }

    public Long getTrainerId() { return trainerId; }
    public void setTrainerId(Long trainerId) { this.trainerId = trainerId; }

    public String getTrainerName() { return trainerName; }
    public void setTrainerName(String trainerName) { this.trainerName = trainerName; }

    public Date getTrainingDate() { return trainingDate; }
    public void setTrainingDate(Date trainingDate) { this.trainingDate = trainingDate; }

    public BigDecimal getTrainingHours() { return trainingHours; }
    public void setTrainingHours(BigDecimal trainingHours) { this.trainingHours = trainingHours; }

    public String getTrainingContent() { return trainingContent; }
    public void setTrainingContent(String trainingContent) { this.trainingContent = trainingContent; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}