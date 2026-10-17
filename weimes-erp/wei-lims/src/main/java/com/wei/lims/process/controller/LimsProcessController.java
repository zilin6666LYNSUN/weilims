package com.wei.lims.process.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.process.domain.LimsProcessApproval;
import com.wei.lims.process.domain.LimsProcessDefinition;
import com.wei.lims.process.domain.LimsProcessInstance;
import com.wei.lims.process.service.ILimsProcessService;
import com.wei.lims.process.service.ILimsProcessService.ProcessInstanceResult;

/**
 * 通用审批引擎控制器
 * 提供流程定义管理(CRUD/激活/停用)与流程实例操作(启动/办理/终止/待办/历史)。
 */
@RestController
@RequestMapping("/lims/process")
public class LimsProcessController extends BaseController {

    @Autowired
    private ILimsProcessService limsProcessService;

    // ==================== 流程定义管理 ====================

    @PreAuthorize("@ss.hasPermi('lims:process:definition:list')")
    @GetMapping("/definition/list")
    public TableDataInfo list(LimsProcessDefinition limsProcessDefinition) {
        startPage();
        List<LimsProcessDefinition> list = limsProcessService.selectLimsProcessDefinitionList(limsProcessDefinition);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:process:definition:query')")
    @GetMapping("/definition/{procDefId}")
    public AjaxResult getInfo(@PathVariable("procDefId") Long procDefId) {
        return AjaxResult.success(limsProcessService.selectLimsProcessDefinitionByProcDefId(procDefId));
    }

    @PreAuthorize("@ss.hasPermi('lims:process:definition:add')")
    @Log(title = "流程定义", businessType = BusinessType.INSERT)
    @PostMapping("/definition")
    public AjaxResult add(@RequestBody LimsProcessDefinition limsProcessDefinition) {
        return toAjax(limsProcessService.insertLimsProcessDefinition(limsProcessDefinition));
    }

    @PreAuthorize("@ss.hasPermi('lims:process:definition:edit')")
    @Log(title = "流程定义", businessType = BusinessType.UPDATE)
    @PutMapping("/definition")
    public AjaxResult edit(@RequestBody LimsProcessDefinition limsProcessDefinition) {
        return toAjax(limsProcessService.updateLimsProcessDefinition(limsProcessDefinition));
    }

    @PreAuthorize("@ss.hasPermi('lims:process:definition:edit')")
    @Log(title = "流程定义", businessType = BusinessType.UPDATE)
    @PutMapping("/definition/{procDefId}/activate")
    public AjaxResult activate(@PathVariable("procDefId") Long procDefId) {
        return toAjax(limsProcessService.activateLimsProcessDefinition(procDefId));
    }

    @PreAuthorize("@ss.hasPermi('lims:process:definition:edit')")
    @Log(title = "流程定义", businessType = BusinessType.UPDATE)
    @PutMapping("/definition/{procDefId}/deactivate")
    public AjaxResult deactivate(@PathVariable("procDefId") Long procDefId) {
        return toAjax(limsProcessService.deactivateLimsProcessDefinition(procDefId));
    }

    @PreAuthorize("@ss.hasPermi('lims:process:definition:remove')")
    @Log(title = "流程定义", businessType = BusinessType.DELETE)
    @DeleteMapping("/definition/{procDefIds}")
    public AjaxResult remove(@PathVariable Long[] procDefIds) {
        return toAjax(limsProcessService.deleteLimsProcessDefinitionByProcDefIds(procDefIds));
    }

    // ==================== 流程实例操作 ====================

    @PostMapping("/instance/start")
    public AjaxResult start(@RequestBody Map<String, Object> body) {
        String procKey = asString(body.get("procKey"));
        String businessType = asString(body.get("businessType"));
        Long businessId = asLong(body.get("businessId"));
        Map<String, Object> variables = asMap(body.get("variables"));
        ProcessInstanceResult result = limsProcessService.startProcess(procKey, businessType, businessId, variables);
        return AjaxResult.success(result);
    }

    @PostMapping("/instance/complete")
    public AjaxResult complete(@RequestBody Map<String, Object> body) {
        String procInstId = asString(body.get("procInstId"));
        String nodeId = asString(body.get("nodeId"));
        String action = asString(body.get("action"));
        String comment = asString(body.get("comment"));
        Map<String, Object> variables = asMap(body.get("variables"));
        ProcessInstanceResult result = limsProcessService.completeTask(procInstId, nodeId, action, comment, variables);
        return AjaxResult.success(result);
    }

    @PostMapping("/instance/terminate")
    public AjaxResult terminate(@RequestBody Map<String, Object> body) {
        String procInstId = asString(body.get("procInstId"));
        String reason = asString(body.get("reason"));
        return toAjax(limsProcessService.terminateProcess(procInstId, reason));
    }

    @PreAuthorize("@ss.hasPermi('lims:process:instance:list')")
    @GetMapping("/instance/list")
    public TableDataInfo instanceList(LimsProcessInstance limsProcessInstance) {
        startPage();
        List<LimsProcessInstance> list = limsProcessService.selectLimsProcessInstanceList(limsProcessInstance);
        return getDataTable(list);
    }

    @GetMapping("/instance/todo")
    public AjaxResult todoList() {
        return AjaxResult.success(limsProcessService.getTodoList());
    }

    @GetMapping("/instance/{procInstId}")
    public AjaxResult instanceInfo(@PathVariable("procInstId") String procInstId) {
        return AjaxResult.success(limsProcessService.getCurrentTaskInfo(procInstId));
    }

    @GetMapping("/approval/history/{procInstId}")
    public AjaxResult history(@PathVariable("procInstId") String procInstId) {
        return AjaxResult.success(limsProcessService.getApprovalHistory(procInstId));
    }

    @GetMapping("/approval/business/{businessType}/{businessId}")
    public AjaxResult historyByBusiness(@PathVariable("businessType") String businessType,
                                        @PathVariable("businessId") Long businessId) {
        return AjaxResult.success(limsProcessService.getApprovalHistoryByBusiness(businessType, businessId));
    }

    private String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private Long asLong(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return Long.valueOf(String.valueOf(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> asMap(Object value) {
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return null;
    }
}
