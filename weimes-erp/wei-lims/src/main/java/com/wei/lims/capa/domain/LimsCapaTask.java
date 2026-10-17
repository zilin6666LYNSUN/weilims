package com.wei.lims.capa.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * CAPA任务对象 lims_capa_task
 */
public class LimsCapaTask {
    private static final long serialVersionUID = 1L;

    private Long taskId;
    private Long capaId;
    private String taskDescription;
    private Long assigneeId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dueDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date completionDate;
    private String status;

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public Long getCapaId() { return capaId; }
    public void setCapaId(Long capaId) { this.capaId = capaId; }
    public String getTaskDescription() { return taskDescription; }
    public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }
    public Long getAssigneeId() { return assigneeId; }
    public void setAssigneeId(Long assigneeId) { this.assigneeId = assigneeId; }
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    public Date getCompletionDate() { return completionDate; }
    public void setCompletionDate(Date completionDate) { this.completionDate = completionDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
