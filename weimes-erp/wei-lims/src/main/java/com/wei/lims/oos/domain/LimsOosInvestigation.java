package com.wei.lims.oos.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wei.common.annotation.Excel;
import com.wei.common.core.domain.BaseEntity;

/**
 * OOS/OOT调查对象 lims_oos_investigation
 */
public class LimsOosInvestigation extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long investigationId;

    @Excel(name = "调查编号")
    private String investigationNo;

    private Long testResultId;

    private Long testOrderId;

    @Excel(name = "类型: OOS/OOT")
    private String type;

    @Excel(name = "调查阶段")
    private String phase;

    private String oosValue;

    private String specificationLimit;

    private String labInvestigation;

    private String isLabCause;

    private String labErrorType;

    private String fullInvestigation;

    private String rootCause;

    private String capaIds;

    private String correctiveAction;

    private String originalResultValid;

    private String retestResult;

    @Excel(name = "结论")
    private String conclusion;

    private Long investigatorId;

    private Long approverId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date closeDate;

    @Excel(name = "状态")
    private String status;

    private String procInstId;

    public Long getInvestigationId() { return investigationId; }
    public void setInvestigationId(Long investigationId) { this.investigationId = investigationId; }
    public String getInvestigationNo() { return investigationNo; }
    public void setInvestigationNo(String investigationNo) { this.investigationNo = investigationNo; }
    public Long getTestResultId() { return testResultId; }
    public void setTestResultId(Long testResultId) { this.testResultId = testResultId; }
    public Long getTestOrderId() { return testOrderId; }
    public void setTestOrderId(Long testOrderId) { this.testOrderId = testOrderId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getPhase() { return phase; }
    public void setPhase(String phase) { this.phase = phase; }
    public String getOosValue() { return oosValue; }
    public void setOosValue(String oosValue) { this.oosValue = oosValue; }
    public String getSpecificationLimit() { return specificationLimit; }
    public void setSpecificationLimit(String specificationLimit) { this.specificationLimit = specificationLimit; }
    public String getLabInvestigation() { return labInvestigation; }
    public void setLabInvestigation(String labInvestigation) { this.labInvestigation = labInvestigation; }
    public String getIsLabCause() { return isLabCause; }
    public void setIsLabCause(String isLabCause) { this.isLabCause = isLabCause; }
    public String getLabErrorType() { return labErrorType; }
    public void setLabErrorType(String labErrorType) { this.labErrorType = labErrorType; }
    public String getFullInvestigation() { return fullInvestigation; }
    public void setFullInvestigation(String fullInvestigation) { this.fullInvestigation = fullInvestigation; }
    public String getRootCause() { return rootCause; }
    public void setRootCause(String rootCause) { this.rootCause = rootCause; }
    public String getCapaIds() { return capaIds; }
    public void setCapaIds(String capaIds) { this.capaIds = capaIds; }
    public String getCorrectiveAction() { return correctiveAction; }
    public void setCorrectiveAction(String correctiveAction) { this.correctiveAction = correctiveAction; }
    public String getOriginalResultValid() { return originalResultValid; }
    public void setOriginalResultValid(String originalResultValid) { this.originalResultValid = originalResultValid; }
    public String getRetestResult() { return retestResult; }
    public void setRetestResult(String retestResult) { this.retestResult = retestResult; }
    public String getConclusion() { return conclusion; }
    public void setConclusion(String conclusion) { this.conclusion = conclusion; }
    public Long getInvestigatorId() { return investigatorId; }
    public void setInvestigatorId(Long investigatorId) { this.investigatorId = investigatorId; }
    public Long getApproverId() { return approverId; }
    public void setApproverId(Long approverId) { this.approverId = approverId; }
    public Date getCloseDate() { return closeDate; }
    public void setCloseDate(Date closeDate) { this.closeDate = closeDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getProcInstId() { return procInstId; }
    public void setProcInstId(String procInstId) { this.procInstId = procInstId; }
}
