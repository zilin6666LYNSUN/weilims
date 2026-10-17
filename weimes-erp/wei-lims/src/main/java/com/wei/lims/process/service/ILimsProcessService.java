package com.wei.lims.process.service;

import java.util.List;
import java.util.Map;
import com.wei.lims.process.domain.LimsProcessApproval;
import com.wei.lims.process.domain.LimsProcessDefinition;
import com.wei.lims.process.domain.LimsProcessInstance;

/**
 * 通用审批引擎Service接口
 * 支撑 lims_process_definition / lims_process_approval / lims_process_instance
 * 三表：流程定义管理 + 流程实例启动/推进/终止 + 审批历史 + 待办查询。
 */
public interface ILimsProcessService {

    // ==================== 流程定义管理 ====================

    public List<LimsProcessDefinition> selectLimsProcessDefinitionList(LimsProcessDefinition limsProcessDefinition);

    public LimsProcessDefinition selectLimsProcessDefinitionByProcDefId(Long procDefId);

    public int insertLimsProcessDefinition(LimsProcessDefinition limsProcessDefinition);

    public int updateLimsProcessDefinition(LimsProcessDefinition limsProcessDefinition);

    /** 激活流程定义(同一KEY下其他ACTIVE定义自动置为INACTIVE) */
    public int activateLimsProcessDefinition(Long procDefId);

    /** 停用流程定义 */
    public int deactivateLimsProcessDefinition(Long procDefId);

    public int deleteLimsProcessDefinitionByProcDefIds(Long[] procDefIds);

    /** 是否存在已生效的流程定义 */
    public boolean hasActiveProcessDefinition(String procKey);

    /** 获取已生效的流程定义(取最新版本) */
    public LimsProcessDefinition getActiveProcessDefinition(String procKey);

    // ==================== 流程实例操作 ====================

    /** 启动流程实例，返回结果(含实例ID/当前节点) */
    public ProcessInstanceResult startProcess(String procKey, String businessType, Long businessId,
                                              Map<String, Object> variables);

    /** 完成当前节点审批(APPROVE/REJECT/RETURN)，引擎负责推进/回退/条件分支/会签 */
    public ProcessInstanceResult completeTask(String procInstId, String nodeId, String action,
                                              String comment, Map<String, Object> variables);

    /** 终止流程实例(业务作废等场景) */
    public int terminateProcess(String procInstId, String reason);

    public LimsProcessInstance getProcessInstance(String procInstId);

    /** 流程实例分页列表 */
    public List<LimsProcessInstance> selectLimsProcessInstanceList(LimsProcessInstance limsProcessInstance);

    /** 当前任务信息(节点/审批人/变量) */
    public Map<String, Object> getCurrentTaskInfo(String procInstId);

    /** 我的待办(RUNNING 且当前节点审批人包含当前用户) */
    public List<LimsProcessInstance> getTodoList();

    /** 流程审批历史 */
    public List<LimsProcessApproval> getApprovalHistory(String procInstId);

    /** 按业务维度查询审批历史 */
    public List<LimsProcessApproval> getApprovalHistoryByBusiness(String businessType, Long businessId);

    /**
     * 引擎执行结果
     */
    class ProcessInstanceResult {
        private String procInstId;
        private String currentNodeId;
        private String currentNodeName;
        private String instanceStatus;
        private boolean passed;
        private String message;

        public ProcessInstanceResult() {
        }

        public ProcessInstanceResult(String procInstId, String currentNodeId, String currentNodeName,
                                     String instanceStatus, boolean passed, String message) {
            this.procInstId = procInstId;
            this.currentNodeId = currentNodeId;
            this.currentNodeName = currentNodeName;
            this.instanceStatus = instanceStatus;
            this.passed = passed;
            this.message = message;
        }

        public String getProcInstId() {
            return procInstId;
        }

        public void setProcInstId(String procInstId) {
            this.procInstId = procInstId;
        }

        public String getCurrentNodeId() {
            return currentNodeId;
        }

        public void setCurrentNodeId(String currentNodeId) {
            this.currentNodeId = currentNodeId;
        }

        public String getCurrentNodeName() {
            return currentNodeName;
        }

        public void setCurrentNodeName(String currentNodeName) {
            this.currentNodeName = currentNodeName;
        }

        public String getInstanceStatus() {
            return instanceStatus;
        }

        public void setInstanceStatus(String instanceStatus) {
            this.instanceStatus = instanceStatus;
        }

        public boolean isPassed() {
            return passed;
        }

        public void setPassed(boolean passed) {
            this.passed = passed;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
