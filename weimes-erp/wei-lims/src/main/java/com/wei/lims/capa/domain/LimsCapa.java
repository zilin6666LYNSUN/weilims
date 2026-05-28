package com.wei.lims.capa.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wei.common.annotation.Excel;
import com.wei.common.core.domain.BaseEntity;

/**
 * CAPA管理对象 lims_capa
 */
public class LimsCapa extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long capaId;

    @Excel(name = "CAPA编号")
    private String capaCode;

    @Excel(name = "类型")
    private String capaType;

    @Excel(name = "来源类型")
    private String sourceType;

    @Excel(name = "来源编号")
    private String sourceCode;

    @Excel(name = "描述")
    private String description;

    @Excel(name = "根本原因")
    private String rootCause;

    @Excel(name = "纠正措施")
    private String correctiveAction;

    @Excel(name = "预防措施")
    private String preventiveAction;

    private Long responsibleId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dueDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date completionDate;

    private String effectivenessResult;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date effectivenessDate;

    @Excel(name = "状态")
    private String status;

    private String procInstId;

    public Long getCapaId() { return capaId; }
    public void setCapaId(Long capaId) { this.capaId = capaId; }
    public String getCapaCode() { return capaCode; }
    public void setCapaCode(String capaCode) { this.capaCode = capaCode; }
    public String getCapaType() { return capaType; }
    public void setCapaType(String capaType) { this.capaType = capaType; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public String getSourceCode() { return sourceCode; }
    public void setSourceCode(String sourceCode) { this.sourceCode = sourceCode; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRootCause() { return rootCause; }
    public void setRootCause(String rootCause) { this.rootCause = rootCause; }
    public String getCorrectiveAction() { return correctiveAction; }
    public void setCorrectiveAction(String correctiveAction) { this.correctiveAction = correctiveAction; }
    public String getPreventiveAction() { return preventiveAction; }
    public void setPreventiveAction(String preventiveAction) { this.preventiveAction = preventiveAction; }
    public Long getResponsibleId() { return responsibleId; }
    public void setResponsibleId(Long responsibleId) { this.responsibleId = responsibleId; }
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    public Date getCompletionDate() { return completionDate; }
    public void setCompletionDate(Date completionDate) { this.completionDate = completionDate; }
    public String getEffectivenessResult() { return effectivenessResult; }
    public void setEffectivenessResult(String effectivenessResult) { this.effectivenessResult = effectivenessResult; }
    public Date getEffectivenessDate() { return effectivenessDate; }
    public void setEffectivenessDate(Date effectivenessDate) { this.effectivenessDate = effectivenessDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getProcInstId() { return procInstId; }
    public void setProcInstId(String procInstId) { this.procInstId = procInstId; }
}
