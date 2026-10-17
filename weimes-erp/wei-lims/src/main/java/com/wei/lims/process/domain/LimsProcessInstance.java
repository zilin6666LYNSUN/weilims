package com.wei.lims.process.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wei.common.core.domain.BaseEntity;

/**
 * 流程实例对象 lims_process_instance
 * 通用审批引擎运行时状态：当前节点、实例状态、流程变量、会签进度。
 */
public class LimsProcessInstance extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 流程实例ID */
    private String procInstId;

    /** 流程定义ID */
    private Long procDefId;

    /** 流程KEY */
    private String procKey;

    /** 业务类型 */
    private String businessType;

    /** 业务ID */
    private Long businessId;

    /** 当前节点ID */
    private String currentNodeId;

    /** 实例状态: RUNNING/COMPLETED/TERMINATED */
    private String status;

    /** 流程变量JSON */
    private String variables;

    /** 会签节点审批进度JSON */
    private String currentApprovals;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
    }

    public Long getProcDefId() {
        return procDefId;
    }

    public void setProcDefId(Long procDefId) {
        this.procDefId = procDefId;
    }

    public String getProcKey() {
        return procKey;
    }

    public void setProcKey(String procKey) {
        this.procKey = procKey;
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

    public String getCurrentNodeId() {
        return currentNodeId;
    }

    public void setCurrentNodeId(String currentNodeId) {
        this.currentNodeId = currentNodeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVariables() {
        return variables;
    }

    public void setVariables(String variables) {
        this.variables = variables;
    }

    public String getCurrentApprovals() {
        return currentApprovals;
    }

    public void setCurrentApprovals(String currentApprovals) {
        this.currentApprovals = currentApprovals;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
