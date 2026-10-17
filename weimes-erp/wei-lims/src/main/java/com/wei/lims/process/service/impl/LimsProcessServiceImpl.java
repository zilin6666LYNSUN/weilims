package com.wei.lims.process.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wei.common.exception.BusinessException;
import com.wei.common.utils.DateUtils;
import com.wei.common.utils.SecurityUtils;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import com.wei.lims.process.domain.LimsProcessApproval;
import com.wei.lims.process.domain.LimsProcessDefinition;
import com.wei.lims.process.domain.LimsProcessInstance;
import com.wei.lims.process.mapper.LimsProcessApprovalMapper;
import com.wei.lims.process.mapper.LimsProcessDefinitionMapper;
import com.wei.lims.process.mapper.LimsProcessInstanceMapper;
import com.wei.lims.process.service.ILimsProcessService;

/**
 * 通用审批引擎核心实现
 *
 * 流程定义(bpmn_xml)JSON结构:
 * {
 *   "version": 1,
 *   "startNode": "N1",
 *   "nodes": [
 *     {"nodeId":"N1","nodeType":"APPROVAL","name":"复核","assigneeType":"USER|ROLE",
 *      "assigneeValue":"1001 或 qa_reviewer(逗号分隔多值)","signMode":"SINGLE|ALL",
 *      "nextOnPass":"N2","nextOnReject":"N1"},
 *     {"nodeId":"N3","nodeType":"CONDITION","name":"条件分支",
 *      "conditions":[{"expression":"conclusion == 'QUALIFIED'","target":"N4"},{"expression":"*","target":"N5"}]},
 *     {"nodeId":"N5","nodeType":"END","name":"结束"}
 *   ]
 * }
 *
 * 节点类型: APPROVAL(审批)/CONDITION(条件分支)/END(结束)
 * 会签: signMode=ALL 时节点内所有审批人全部 APPROVE 才推进，任一 REJECT 即回退
 * 条件表达式: 形如 "conclusion == 'QUALIFIED'"，* 为默认分支(须放最后兜底)
 */
@Service
public class LimsProcessServiceImpl implements ILimsProcessService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String STATUS_RUNNING = "RUNNING";
    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String STATUS_TERMINATED = "TERMINATED";
    private static final String STATUS_DRAFT = "DRAFT";
    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_INACTIVE = "INACTIVE";

    private static final String ACTION_APPROVE = "APPROVE";
    private static final String ACTION_REJECT = "REJECT";
    private static final String ACTION_RETURN = "RETURN";

    @Autowired
    private LimsProcessDefinitionMapper limsProcessDefinitionMapper;

    @Autowired
    private LimsProcessInstanceMapper limsProcessInstanceMapper;

    @Autowired
    private LimsProcessApprovalMapper limsProcessApprovalMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    // ==================== 流程定义管理 ====================

    @Override
    public List<LimsProcessDefinition> selectLimsProcessDefinitionList(LimsProcessDefinition limsProcessDefinition) {
        return limsProcessDefinitionMapper.selectLimsProcessDefinitionList(limsProcessDefinition);
    }

    @Override
    public LimsProcessDefinition selectLimsProcessDefinitionByProcDefId(Long procDefId) {
        return limsProcessDefinitionMapper.selectLimsProcessDefinitionByProcDefId(procDefId);
    }

    @Override
    @Transactional
    public int insertLimsProcessDefinition(LimsProcessDefinition limsProcessDefinition) {
        if (limsProcessDefinition.getProcKey() == null || limsProcessDefinition.getProcKey().isEmpty()) {
            throw new BusinessException("流程KEY不能为空");
        }
        if (limsProcessDefinition.getBpmnXml() == null || !isValidDefinitionJson(limsProcessDefinition.getBpmnXml())) {
            throw new BusinessException("流程定义JSON格式不合法");
        }
        LimsProcessDefinition exists = limsProcessDefinitionMapper.selectLimsProcessDefinitionByProcKey(limsProcessDefinition.getProcKey());
        limsProcessDefinition.setVersion(exists == null ? 1 : exists.getVersion() + 1);
        if (limsProcessDefinition.getStatus() == null) {
            limsProcessDefinition.setStatus(STATUS_DRAFT);
        }
        limsProcessDefinition.setCreateBy(SecurityUtils.getUsername());
        limsProcessDefinition.setCreateTime(DateUtils.getNowDate());
        int rows = limsProcessDefinitionMapper.insertLimsProcessDefinition(limsProcessDefinition);
        limsAuditTrailService.recordFieldChanges("lims_process_definition", limsProcessDefinition.getProcDefId(),
                "INSERT", null, limsProcessDefinition, "新建流程定义: " + limsProcessDefinition.getProcName());
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsProcessDefinition(LimsProcessDefinition limsProcessDefinition) {
        LimsProcessDefinition exists = selectLimsProcessDefinitionByProcDefId(limsProcessDefinition.getProcDefId());
        if (exists == null) {
            throw new BusinessException("流程定义不存在");
        }
        if (limsProcessDefinition.getBpmnXml() != null && !isValidDefinitionJson(limsProcessDefinition.getBpmnXml())) {
            throw new BusinessException("流程定义JSON格式不合法");
        }
        limsProcessDefinition.setUpdateBy(SecurityUtils.getUsername());
        limsProcessDefinition.setUpdateTime(DateUtils.getNowDate());
        int rows = limsProcessDefinitionMapper.updateLimsProcessDefinition(limsProcessDefinition);
        limsAuditTrailService.recordFieldChanges("lims_process_definition", limsProcessDefinition.getProcDefId(),
                "UPDATE", exists, limsProcessDefinition, "更新流程定义: " + limsProcessDefinition.getProcName());
        return rows;
    }

    @Override
    @Transactional
    public int activateLimsProcessDefinition(Long procDefId) {
        LimsProcessDefinition target = selectLimsProcessDefinitionByProcDefId(procDefId);
        if (target == null) {
            throw new BusinessException("流程定义不存在");
        }
        if (!isValidDefinitionJson(target.getBpmnXml())) {
            throw new BusinessException("流程定义JSON格式不合法，无法激活");
        }
        // 同一KEY下其他ACTIVE定义置为INACTIVE，保证唯一生效版本
        LimsProcessDefinition query = new LimsProcessDefinition();
        query.setProcKey(target.getProcKey());
        query.setStatus(STATUS_ACTIVE);
        List<LimsProcessDefinition> actives = limsProcessDefinitionMapper.selectLimsProcessDefinitionList(query);
        if (actives != null) {
            for (LimsProcessDefinition active : actives) {
                if (!active.getProcDefId().equals(procDefId)) {
                    active.setStatus(STATUS_INACTIVE);
                    active.setUpdateBy(SecurityUtils.getUsername());
                    active.setUpdateTime(DateUtils.getNowDate());
                    limsProcessDefinitionMapper.updateLimsProcessDefinition(active);
                }
            }
        }
        target.setStatus(STATUS_ACTIVE);
        target.setEffectiveDate(new Date());
        target.setUpdateBy(SecurityUtils.getUsername());
        target.setUpdateTime(DateUtils.getNowDate());
        int rows = limsProcessDefinitionMapper.updateLimsProcessDefinition(target);
        limsAuditTrailService.recordFieldChanges("lims_process_definition", procDefId,
                "UPDATE", null, target, "激活流程定义: " + target.getProcName());
        return rows;
    }

    @Override
    @Transactional
    public int deactivateLimsProcessDefinition(Long procDefId) {
        LimsProcessDefinition target = selectLimsProcessDefinitionByProcDefId(procDefId);
        if (target == null) {
            throw new BusinessException("流程定义不存在");
        }
        target.setStatus(STATUS_INACTIVE);
        target.setUpdateBy(SecurityUtils.getUsername());
        target.setUpdateTime(DateUtils.getNowDate());
        int rows = limsProcessDefinitionMapper.updateLimsProcessDefinition(target);
        limsAuditTrailService.recordFieldChanges("lims_process_definition", procDefId,
                "UPDATE", null, target, "停用流程定义: " + target.getProcName());
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsProcessDefinitionByProcDefIds(Long[] procDefIds) {
        if (procDefIds == null || procDefIds.length == 0) {
            throw new BusinessException("请选择流程定义");
        }
        for (Long procDefId : procDefIds) {
            LimsProcessInstance query = new LimsProcessInstance();
            query.setProcDefId(procDefId);
            List<LimsProcessInstance> instances = limsProcessInstanceMapper.selectLimsProcessInstanceList(query);
            if (instances != null && !instances.isEmpty()) {
                throw new BusinessException("流程定义已被实例引用，禁止删除: " + procDefId);
            }
        }
        int rows = limsProcessDefinitionMapper.deleteLimsProcessDefinitionByProcDefIds(procDefIds);
        for (Long procDefId : procDefIds) {
            limsAuditTrailService.recordFieldChanges("lims_process_definition", procDefId,
                    "DELETE", null, null, "删除流程定义: " + procDefId);
        }
        return rows;
    }

    @Override
    public boolean hasActiveProcessDefinition(String procKey) {
        return limsProcessDefinitionMapper.selectActiveLimsProcessDefinitionByProcKey(procKey) != null;
    }

    @Override
    public LimsProcessDefinition getActiveProcessDefinition(String procKey) {
        return limsProcessDefinitionMapper.selectActiveLimsProcessDefinitionByProcKey(procKey);
    }

    // ==================== 流程实例操作 ====================

    @Override
    @Transactional
    public ProcessInstanceResult startProcess(String procKey, String businessType, Long businessId,
                                              Map<String, Object> variables) {
        LimsProcessDefinition definition = limsProcessDefinitionMapper.selectActiveLimsProcessDefinitionByProcKey(procKey);
        if (definition == null) {
            throw new BusinessException("流程定义不存在或未生效: " + procKey);
        }
        JsonNode root = parseDefinition(definition.getBpmnXml());
        String startNodeId = root.path("startNode").asText("");
        if (startNodeId.isEmpty()) {
            throw new BusinessException("流程定义缺少起始节点 startNode");
        }
        JsonNode startNode = findNode(root, startNodeId);
        if (startNode == null) {
            throw new BusinessException("起始节点不存在: " + startNodeId);
        }
        if (!"APPROVAL".equals(startNode.path("nodeType").asText())) {
            throw new BusinessException("起始节点必须是审批(APPROVAL)节点");
        }

        String procInstId = generateProcInstId();
        LimsProcessInstance instance = new LimsProcessInstance();
        instance.setProcInstId(procInstId);
        instance.setProcDefId(definition.getProcDefId());
        instance.setProcKey(procKey);
        instance.setBusinessType(businessType);
        instance.setBusinessId(businessId);
        instance.setCurrentNodeId(startNodeId);
        instance.setStatus(STATUS_RUNNING);
        instance.setVariables(writeJson(variables == null ? new HashMap<String, Object>() : variables));
        instance.setCurrentApprovals("{}");
        instance.setStartTime(new Date());
        instance.setCreateBy(SecurityUtils.getUsername());
        instance.setCreateTime(DateUtils.getNowDate());
        limsProcessInstanceMapper.insertLimsProcessInstance(instance);

        return new ProcessInstanceResult(procInstId, startNodeId, startNode.path("name").asText(""),
                STATUS_RUNNING, true, "流程启动成功，当前节点: " + startNode.path("name").asText(""));
    }

    @Override
    @Transactional
    public ProcessInstanceResult completeTask(String procInstId, String nodeId, String action,
                                              String comment, Map<String, Object> variables) {
        LimsProcessInstance instance = requireRunningInstance(procInstId);
        LimsProcessDefinition definition = limsProcessDefinitionMapper.selectLimsProcessDefinitionByProcDefId(instance.getProcDefId());
        if (definition == null) {
            throw new BusinessException("流程定义已不存在");
        }
        JsonNode root = parseDefinition(definition.getBpmnXml());

        String currentNodeId = (nodeId == null || nodeId.isEmpty()) ? instance.getCurrentNodeId() : nodeId;
        if (!currentNodeId.equals(instance.getCurrentNodeId())) {
            throw new BusinessException("当前待办节点不匹配，请按当前节点办理");
        }
        JsonNode node = findNode(root, currentNodeId);
        if (node == null) {
            throw new BusinessException("节点不存在: " + currentNodeId);
        }
        if (!"APPROVAL".equals(node.path("nodeType").asText())) {
            throw new BusinessException("该节点不是审批节点，不可直接办理");
        }

        // 审批人校验
        Long userId = SecurityUtils.getUserId();
        List<Long> assigneeIds = resolveAssignees(node);
        if (!assigneeIds.contains(userId)) {
            throw new BusinessException("当前用户不是该节点审批人");
        }

        String nodeName = node.path("name").asText("");
        String normalizedAction = normalizeAction(action);
        boolean pass = ACTION_APPROVE.equals(normalizedAction);
        long startedAt = System.currentTimeMillis();

        // 流程变量合并
        Map<String, Object> varMap = readJsonMap(instance.getVariables());
        if (variables != null) {
            varMap.putAll(variables);
        }

        String targetId;
        boolean allApproved = true;
        if (pass) {
            if (isAllSign(node)) {
                // 会签节点: 记录本次通过，等待全部审批人
                Map<String, Object> approvals = readJsonMap(instance.getCurrentApprovals());
                if (approvals.containsKey(String.valueOf(userId))) {
                    throw new BusinessException("您已在该节点审批过，请勿重复提交");
                }
                approvals.put(String.valueOf(userId), ACTION_APPROVE);
                instance.setCurrentApprovals(writeJson(approvals));
                if (approvals.size() < assigneeIds.size()) {
                    allApproved = false;
                }
            } else if (hasApprovedBefore(procInstId, nodeName, userId)) {
                throw new BusinessException("您已在该节点审批过，请勿重复提交");
            }
            targetId = node.path("nextOnPass").asText("");
        } else {
            // REJECT/RETURN 回退，会签进度清零
            targetId = node.path("nextOnReject").asText("");
            instance.setCurrentApprovals("{}");
        }
        if (targetId.isEmpty()) {
            throw new BusinessException("节点未配置流转目标(nextOnPass/nextOnReject)");
        }

        if (allApproved) {
            // 推进至目标节点（目标为条件分支时按表达式路由）
            JsonNode targetNode = findNode(root, targetId);
            if (targetNode == null) {
                throw new BusinessException("流转目标节点不存在: " + targetId);
            }
            if ("CONDITION".equals(targetNode.path("nodeType").asText())) {
                targetId = routeCondition(targetNode, varMap);
                targetNode = findNode(root, targetId);
                if (targetNode == null) {
                    throw new BusinessException("条件分支目标节点不存在: " + targetId);
                }
            }
            instance.setCurrentNodeId(targetId);
            String targetType = targetNode.path("nodeType").asText("");
            if ("END".equals(targetType)) {
                instance.setStatus(STATUS_COMPLETED);
                instance.setEndTime(new Date());
            }
        }

        instance.setVariables(writeJson(varMap));
        instance.setUpdateBy(SecurityUtils.getUsername());
        instance.setUpdateTime(DateUtils.getNowDate());
        limsProcessInstanceMapper.updateLimsProcessInstance(instance);

        // 记录审批历史 + 审计
        recordApproval(instance, nodeName, userId, normalizedAction, comment, startedAt);

        String message;
        if (!allApproved) {
            message = "会签等待其余审批人通过";
        } else if (STATUS_COMPLETED.equals(instance.getStatus())) {
            message = "流程结束";
        } else {
            message = "审批通过，流转至 " + findNode(root, instance.getCurrentNodeId()).path("name").asText("");
        }
        return new ProcessInstanceResult(procInstId, instance.getCurrentNodeId(),
                findNode(root, instance.getCurrentNodeId()).path("name").asText(""),
                instance.getStatus(), allApproved, message);
    }

    @Override
    @Transactional
    public int terminateProcess(String procInstId, String reason) {
        LimsProcessInstance instance = selectLimsProcessInstanceByProcInstId(procInstId);
        if (instance == null) {
            throw new BusinessException("流程实例不存在");
        }
        if (!STATUS_RUNNING.equals(instance.getStatus())) {
            throw new BusinessException("流程实例已结束，不能终止");
        }
        instance.setStatus(STATUS_TERMINATED);
        instance.setEndTime(new Date());
        instance.setUpdateBy(SecurityUtils.getUsername());
        instance.setUpdateTime(DateUtils.getNowDate());
        int rows = limsProcessInstanceMapper.updateLimsProcessInstance(instance);

        LimsProcessApproval approval = new LimsProcessApproval();
        approval.setProcInstId(procInstId);
        approval.setBusinessType(instance.getBusinessType());
        approval.setBusinessId(instance.getBusinessId());
        approval.setTaskName(instance.getCurrentNodeId());
        approval.setAssigneeId(SecurityUtils.getUserId());
        approval.setAssigneeName(SecurityUtils.getUsername());
        approval.setAction(ACTION_RETURN);
        approval.setComment("流程终止: " + (reason == null ? "" : reason));
        approval.setApprovalTime(DateUtils.getNowDate());
        limsProcessApprovalMapper.insertLimsProcessApproval(approval);
        limsAuditTrailService.recordFieldChanges("lims_process_approval", approval.getApprovalId(),
                "INSERT", null, approval, "流程终止: " + procInstId);
        return rows;
    }

    @Override
    public List<LimsProcessInstance> selectLimsProcessInstanceList(LimsProcessInstance limsProcessInstance) {
        return limsProcessInstanceMapper.selectLimsProcessInstanceList(limsProcessInstance);
    }

    @Override
    public LimsProcessInstance getProcessInstance(String procInstId) {
        return selectLimsProcessInstanceByProcInstId(procInstId);
    }

    @Override
    public Map<String, Object> getCurrentTaskInfo(String procInstId) {
        LimsProcessInstance instance = selectLimsProcessInstanceByProcInstId(procInstId);
        if (instance == null) {
            throw new BusinessException("流程实例不存在");
        }
        Map<String, Object> info = new HashMap<String, Object>();
        info.put("procInstId", procInstId);
        info.put("procKey", instance.getProcKey());
        info.put("businessType", instance.getBusinessType());
        info.put("businessId", instance.getBusinessId());
        info.put("currentNodeId", instance.getCurrentNodeId());
        info.put("instanceStatus", instance.getStatus());
        info.put("variables", readJsonMap(instance.getVariables()));
        if (STATUS_RUNNING.equals(instance.getStatus())) {
            LimsProcessDefinition definition = limsProcessDefinitionMapper.selectLimsProcessDefinitionByProcDefId(instance.getProcDefId());
            if (definition != null) {
                JsonNode root = parseDefinition(definition.getBpmnXml());
                JsonNode node = findNode(root, instance.getCurrentNodeId());
                if (node != null) {
                    info.put("currentNodeName", node.path("name").asText(""));
                    info.put("assignees", resolveAssignees(node));
                    info.put("signMode", node.path("signMode").asText("SINGLE"));
                    info.put("businessStatus", node.path("businessStatus").asText(""));
                }
            }
        }
        return info;
    }

    @Override
    public List<LimsProcessInstance> getTodoList() {
        Long userId = SecurityUtils.getUserId();
        LimsProcessInstance query = new LimsProcessInstance();
        List<LimsProcessInstance> runningList = limsProcessInstanceMapper.selectRunningLimsProcessInstanceList(query);
        List<LimsProcessInstance> result = new ArrayList<LimsProcessInstance>();
        if (runningList == null) {
            return result;
        }
        for (LimsProcessInstance instance : runningList) {
            LimsProcessDefinition definition = limsProcessDefinitionMapper.selectLimsProcessDefinitionByProcDefId(instance.getProcDefId());
            if (definition == null) {
                continue;
            }
            try {
                JsonNode root = parseDefinition(definition.getBpmnXml());
                JsonNode node = findNode(root, instance.getCurrentNodeId());
                if (node != null && resolveAssignees(node).contains(userId)) {
                    result.add(instance);
                }
            } catch (Exception ignored) {
                // 定义不合法或解析失败时跳过该实例
            }
        }
        return result;
    }

    @Override
    public List<LimsProcessApproval> getApprovalHistory(String procInstId) {
        return limsProcessApprovalMapper.selectLimsProcessApprovalListByProcInstId(procInstId);
    }

    @Override
    public List<LimsProcessApproval> getApprovalHistoryByBusiness(String businessType, Long businessId) {
        return limsProcessApprovalMapper.selectLimsProcessApprovalListByBusiness(businessType, businessId);
    }

    // ==================== 内部工具方法 ====================

    private LimsProcessInstance selectLimsProcessInstanceByProcInstId(String procInstId) {
        return limsProcessInstanceMapper.selectLimsProcessInstanceByProcInstId(procInstId);
    }

    private LimsProcessInstance requireRunningInstance(String procInstId) {
        LimsProcessInstance instance = selectLimsProcessInstanceByProcInstId(procInstId);
        if (instance == null) {
            throw new BusinessException("流程实例不存在");
        }
        if (!STATUS_RUNNING.equals(instance.getStatus())) {
            throw new BusinessException("流程实例已结束，不能办理");
        }
        return instance;
    }

    private void recordApproval(LimsProcessInstance instance, String nodeName, Long userId,
                                String action, String comment, long startedAt) {
        LimsProcessApproval approval = new LimsProcessApproval();
        approval.setProcInstId(instance.getProcInstId());
        approval.setBusinessType(instance.getBusinessType());
        approval.setBusinessId(instance.getBusinessId());
        approval.setTaskName(nodeName);
        approval.setAssigneeId(userId);
        approval.setAssigneeName(SecurityUtils.getUsername());
        approval.setAction(action);
        approval.setComment(comment);
        approval.setApprovalTime(DateUtils.getNowDate());
        approval.setDurationMs(System.currentTimeMillis() - startedAt);
        limsProcessApprovalMapper.insertLimsProcessApproval(approval);
        limsAuditTrailService.recordFieldChanges("lims_process_approval", approval.getApprovalId(),
                "INSERT", null, approval, "流程审批: " + action + " - " + nodeName + " (" + instance.getProcInstId() + ")");
    }

    private boolean hasApprovedBefore(String procInstId, String nodeName, Long userId) {
        LimsProcessApproval query = new LimsProcessApproval();
        query.setProcInstId(procInstId);
        query.setAssigneeId(userId);
        List<LimsProcessApproval> records = limsProcessApprovalMapper.selectLimsProcessApprovalList(query);
        if (records != null) {
            for (LimsProcessApproval record : records) {
                if (nodeName.equals(record.getTaskName()) && ACTION_APPROVE.equals(record.getAction())) {
                    return true;
                }
            }
        }
        return false;
    }

    private String generateProcInstId() {
        return "PI" + DateUtils.dateTimeNow("yyyyMMddHHmmss") + String.format("%04d", System.currentTimeMillis() % 10000);
    }

    private String normalizeAction(String action) {
        if (action == null) {
            throw new BusinessException("审批动作不能为空");
        }
        String upper = action.trim().toUpperCase();
        if (ACTION_APPROVE.equals(upper) || ACTION_REJECT.equals(upper) || ACTION_RETURN.equals(upper)) {
            return upper;
        }
        throw new BusinessException("不支持的审批动作: " + action + " (APPROVE/REJECT/RETURN)");
    }

    private boolean isAllSign(JsonNode node) {
        return "ALL".equalsIgnoreCase(node.path("signMode").asText("SINGLE"));
    }

    private List<Long> resolveAssignees(JsonNode node) {
        String assigneeType = node.path("assigneeType").asText("USER");
        String assigneeValue = node.path("assigneeValue").asText("");
        if (assigneeValue.isEmpty()) {
            throw new BusinessException("节点未配置审批人: " + node.path("name").asText(""));
        }
        Set<Long> ids = new LinkedHashSet<Long>();
        for (String item : assigneeValue.split(",")) {
            String value = item.trim();
            if (value.isEmpty()) {
                continue;
            }
            if ("USER".equalsIgnoreCase(assigneeType)) {
                try {
                    ids.add(Long.valueOf(value));
                } catch (NumberFormatException e) {
                    throw new BusinessException("USER审批人配置需为数字用户ID: " + value);
                }
            } else if ("ROLE".equalsIgnoreCase(assigneeType)) {
                List<Long> roleUserIds = limsProcessDefinitionMapper.selectUserIdsByRoleKey(value);
                if (roleUserIds != null) {
                    ids.addAll(roleUserIds);
                }
            } else {
                throw new BusinessException("不支持的审批人类型: " + assigneeType + " (USER/ROLE)");
            }
        }
        if (ids.isEmpty()) {
            throw new BusinessException("节点未解析到有效审批人: " + node.path("name").asText(""));
        }
        return new ArrayList<Long>(ids);
    }

    private String routeCondition(JsonNode conditionNode, Map<String, Object> vars) {
        JsonNode conditions = conditionNode.path("conditions");
        if (!conditions.isArray()) {
            throw new BusinessException("条件节点未配置 conditions 数组");
        }
        // 先匹配非默认分支，再兜底默认分支
        for (JsonNode cond : conditions) {
            String expr = cond.path("expression").asText("");
            if (!"*".equals(expr.trim()) && evalExpression(expr, vars)) {
                return cond.path("target").asText("");
            }
        }
        for (JsonNode cond : conditions) {
            String expr = cond.path("expression").asText("");
            if ("*".equals(expr.trim())) {
                return cond.path("target").asText("");
            }
        }
        throw new BusinessException("条件分支无匹配项且未配置默认分支(*)");
    }

    /**
     * 简单条件表达式求值：left op right
     * 支持 ==、!=、&gt;、&gt;=、&lt;、&lt;=；字符串用单/双引号包裹。
     */
    private boolean evalExpression(String expression, Map<String, Object> vars) {
        if (expression == null || expression.trim().isEmpty()) {
            return false;
        }
        String expr = expression.trim();
        String op = null;
        int idx = -1;
        if (expr.contains("==")) {
            op = "==";
            idx = expr.indexOf("==");
        } else if (expr.contains("!=")) {
            op = "!=";
            idx = expr.indexOf("!=");
        } else if (expr.contains(">=")) {
            op = ">=";
            idx = expr.indexOf(">=");
        } else if (expr.contains("<=")) {
            op = "<=";
            idx = expr.indexOf("<=");
        } else if (expr.contains(">")) {
            op = ">";
            idx = expr.indexOf(">");
        } else if (expr.contains("<")) {
            op = "<";
            idx = expr.indexOf("<");
        }
        if (op == null || idx <= 0) {
            return false;
        }
        String left = expr.substring(0, idx).trim();
        String right = expr.substring(idx + op.length()).trim();
        if (right.length() >= 2
                && ((right.startsWith("'") && right.endsWith("'")) || (right.startsWith("\"") && right.endsWith("\"")))) {
            right = right.substring(1, right.length() - 1);
        }
        Object actual = vars.get(left);
        String actualStr = actual == null ? "" : String.valueOf(actual);
        if ("==".equals(op)) {
            return actualStr.equals(right);
        }
        if ("!=".equals(op)) {
            return !actualStr.equals(right);
        }
        try {
            double actualNum = Double.parseDouble(actualStr);
            double targetNum = Double.parseDouble(right);
            if (">".equals(op)) {
                return actualNum > targetNum;
            }
            if (">=".equals(op)) {
                return actualNum >= targetNum;
            }
            if ("<".equals(op)) {
                return actualNum < targetNum;
            }
            if ("<=".equals(op)) {
                return actualNum <= targetNum;
            }
        } catch (NumberFormatException ignored) {
            // 非数值比较按字符串处理
        }
        return false;
    }

    private boolean isValidDefinitionJson(String xml) {
        if (xml == null || xml.trim().isEmpty()) {
            return false;
        }
        try {
            JsonNode root = OBJECT_MAPPER.readTree(xml);
            if (!root.has("startNode") || !root.has("nodes") || !root.path("nodes").isArray()) {
                return false;
            }
            return root.path("nodes").size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private JsonNode parseDefinition(String xml) {
        try {
            return OBJECT_MAPPER.readTree(xml);
        } catch (JsonProcessingException e) {
            throw new BusinessException("流程定义JSON解析失败");
        }
    }

    private JsonNode findNode(JsonNode root, String nodeId) {
        JsonNode nodes = root.path("nodes");
        if (!nodes.isArray()) {
            return null;
        }
        for (JsonNode node : nodes) {
            if (nodeId.equals(node.path("nodeId").asText(""))) {
                return node;
            }
        }
        return null;
    }

    private Map<String, Object> readJsonMap(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new HashMap<String, Object>();
        }
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>() { });
        } catch (JsonProcessingException e) {
            return new HashMap<String, Object>();
        }
    }

    private String writeJson(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new BusinessException("JSON序列化失败");
        }
    }
}
