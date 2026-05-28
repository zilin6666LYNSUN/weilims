package com.wei.lims.change.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wei.common.annotation.Excel;
import com.wei.common.core.domain.BaseEntity;

/**
 * 变更控制对象 lims_change_control
 */
public class LimsChangeControl extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long changeId;

    @Excel(name = "变更编号")
    private String changeCode;

    @Excel(name = "变更标题")
    private String changeTitle;

    @Excel(name = "变更类型")
    private String changeType;

    @Excel(name = "变更分类")
    private String changeCategory;

    private String changeDescription;

    private String currentState;

    private String proposedChange;

    @Excel(name = "变更原因")
    private String changeReason;

    @Excel(name = "风险评估")
    private String riskAssessment;

    private String impactAnalysis;

    @Excel(name = "实施计划")
    private String implementationPlan;

    private String trainingNeeded;

    private String revalidationNeeded;

    private String regulatoryImpact;

    private Long requesterId;

    private Long approverId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date effectiveDate;

    @Excel(name = "状态")
    private String status;

    private String procInstId;

    public Long getChangeId() { return changeId; }
    public void setChangeId(Long changeId) { this.changeId = changeId; }
    public String getChangeCode() { return changeCode; }
    public void setChangeCode(String changeCode) { this.changeCode = changeCode; }
    public String getChangeTitle() { return changeTitle; }
    public void setChangeTitle(String changeTitle) { this.changeTitle = changeTitle; }
    public String getChangeType() { return changeType; }
    public void setChangeType(String changeType) { this.changeType = changeType; }
    public String getChangeCategory() { return changeCategory; }
    public void setChangeCategory(String changeCategory) { this.changeCategory = changeCategory; }
    public String getChangeDescription() { return changeDescription; }
    public void setChangeDescription(String changeDescription) { this.changeDescription = changeDescription; }
    public String getCurrentState() { return currentState; }
    public void setCurrentState(String currentState) { this.currentState = currentState; }
    public String getProposedChange() { return proposedChange; }
    public void setProposedChange(String proposedChange) { this.proposedChange = proposedChange; }
    public String getChangeReason() { return changeReason; }
    public void setChangeReason(String changeReason) { this.changeReason = changeReason; }
    public String getRiskAssessment() { return riskAssessment; }
    public void setRiskAssessment(String riskAssessment) { this.riskAssessment = riskAssessment; }
    public String getImpactAnalysis() { return impactAnalysis; }
    public void setImpactAnalysis(String impactAnalysis) { this.impactAnalysis = impactAnalysis; }
    public String getImplementationPlan() { return implementationPlan; }
    public void setImplementationPlan(String implementationPlan) { this.implementationPlan = implementationPlan; }
    public String getTrainingNeeded() { return trainingNeeded; }
    public void setTrainingNeeded(String trainingNeeded) { this.trainingNeeded = trainingNeeded; }
    public String getRevalidationNeeded() { return revalidationNeeded; }
    public void setRevalidationNeeded(String revalidationNeeded) { this.revalidationNeeded = revalidationNeeded; }
    public String getRegulatoryImpact() { return regulatoryImpact; }
    public void setRegulatoryImpact(String regulatoryImpact) { this.regulatoryImpact = regulatoryImpact; }
    public Long getRequesterId() { return requesterId; }
    public void setRequesterId(Long requesterId) { this.requesterId = requesterId; }
    public Long getApproverId() { return approverId; }
    public void setApproverId(Long approverId) { this.approverId = approverId; }
    public Date getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(Date effectiveDate) { this.effectiveDate = effectiveDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getProcInstId() { return procInstId; }
    public void setProcInstId(String procInstId) { this.procInstId = procInstId; }
}
