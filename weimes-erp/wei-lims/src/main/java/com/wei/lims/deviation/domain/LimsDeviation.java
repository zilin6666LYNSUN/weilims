package com.wei.lims.deviation.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wei.common.annotation.Excel;
import com.wei.common.core.domain.BaseEntity;

/**
 * 偏差管理对象 lims_deviation
 */
public class LimsDeviation extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long deviationId;

    @Excel(name = "偏差编号")
    private String deviationCode;

    @Excel(name = "偏差标题")
    private String deviationTitle;

    @Excel(name = "偏差类型")
    private String deviationType;

    @Excel(name = "偏差分类")
    private String deviationCategory;

    @Excel(name = "严重等级")
    private String severityLevel;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date discoveryDate;

    private String discoverySource;

    private String affectedProduct;

    private String affectedBatch;

    @Excel(name = "偏差描述")
    private String description;

    private String immediateAction;

    private String isCapaNeeded;

    private String investigationResult;

    @Excel(name = "根本原因")
    private String rootCause;

    private String capaIds;

    private String effectivenessReview;

    private Long ownerId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dueDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date closeDate;

    private String procInstId;

    @Excel(name = "状态")
    private String status;

    public Long getDeviationId() { return deviationId; }
    public void setDeviationId(Long deviationId) { this.deviationId = deviationId; }
    public String getDeviationCode() { return deviationCode; }
    public void setDeviationCode(String deviationCode) { this.deviationCode = deviationCode; }
    public String getDeviationTitle() { return deviationTitle; }
    public void setDeviationTitle(String deviationTitle) { this.deviationTitle = deviationTitle; }
    public String getDeviationType() { return deviationType; }
    public void setDeviationType(String deviationType) { this.deviationType = deviationType; }
    public String getDeviationCategory() { return deviationCategory; }
    public void setDeviationCategory(String deviationCategory) { this.deviationCategory = deviationCategory; }
    public String getSeverityLevel() { return severityLevel; }
    public void setSeverityLevel(String severityLevel) { this.severityLevel = severityLevel; }
    public Date getDiscoveryDate() { return discoveryDate; }
    public void setDiscoveryDate(Date discoveryDate) { this.discoveryDate = discoveryDate; }
    public String getDiscoverySource() { return discoverySource; }
    public void setDiscoverySource(String discoverySource) { this.discoverySource = discoverySource; }
    public String getAffectedProduct() { return affectedProduct; }
    public void setAffectedProduct(String affectedProduct) { this.affectedProduct = affectedProduct; }
    public String getAffectedBatch() { return affectedBatch; }
    public void setAffectedBatch(String affectedBatch) { this.affectedBatch = affectedBatch; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImmediateAction() { return immediateAction; }
    public void setImmediateAction(String immediateAction) { this.immediateAction = immediateAction; }
    public String getIsCapaNeeded() { return isCapaNeeded; }
    public void setIsCapaNeeded(String isCapaNeeded) { this.isCapaNeeded = isCapaNeeded; }
    public String getInvestigationResult() { return investigationResult; }
    public void setInvestigationResult(String investigationResult) { this.investigationResult = investigationResult; }
    public String getRootCause() { return rootCause; }
    public void setRootCause(String rootCause) { this.rootCause = rootCause; }
    public String getCapaIds() { return capaIds; }
    public void setCapaIds(String capaIds) { this.capaIds = capaIds; }
    public String getEffectivenessReview() { return effectivenessReview; }
    public void setEffectivenessReview(String effectivenessReview) { this.effectivenessReview = effectivenessReview; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    public Date getCloseDate() { return closeDate; }
    public void setCloseDate(Date closeDate) { this.closeDate = closeDate; }
    public String getProcInstId() { return procInstId; }
    public void setProcInstId(String procInstId) { this.procInstId = procInstId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
