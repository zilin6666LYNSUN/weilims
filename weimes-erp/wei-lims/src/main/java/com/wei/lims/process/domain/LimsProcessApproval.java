package com.wei.lims.process.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wei.common.annotation.Excel;
import com.wei.common.core.domain.BaseEntity;

/**
 * 流程审批记录对象 lims_process_approval
 * 通用审批引擎每次审批动作 INSERT 一条历史记录。
 */
public class LimsProcessApproval extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 审批记录ID */
    private Long approvalId;

    /** 流程实例ID */
    @Excel(name = "流程实例ID")
    private String procInstId;

    /** 业务类型 */
    @Excel(name = "业务类型")
    private String businessType;

    /** 业务ID */
    private Long businessId;

    /** 当前节点名称 */
    @Excel(name = "节点名称")
    private String taskName;

    /** 审批人ID */
    private Long assigneeId;

    /** 审批人名称 */
    @Excel(name = "审批人")
    private String assigneeName;

    /** 动作: APPROVE/REJECT/DELEGATE/RETURN */
    @Excel(name = "动作")
    private String action;

    /** 审批意见 */
    private String comment;

    /** 电子签名ID(可选，报告等业务另行维护签章表) */
    private Long signatureId;

    /** 审批时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date approvalTime;

    /** 审批耗时(毫秒) */
    private Long durationMs;

    public Long getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(Long approvalId) {
        this.approvalId = approvalId;
    }

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getSignatureId() {
        return signatureId;
    }

    public void setSignatureId(Long signatureId) {
        this.signatureId = signatureId;
    }

    public Date getApprovalTime() {
        return approvalTime;
    }

    public void setApprovalTime(Date approvalTime) {
        this.approvalTime = approvalTime;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }
}
