package com.wei.pad.mes.controller.pro.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.wei.common.annotation.Log;
import com.wei.common.constant.UserConstants;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.common.enums.mes.OrderStatusEnum;
import com.wei.common.utils.StringUtils;
import com.wei.mes.md.domain.MdWorkstation;
import com.wei.mes.md.service.IMdWorkstationService;
import com.wei.mes.pro.domain.ProFeedback;
import com.wei.mes.pro.domain.ProRouteProcess;
import com.wei.mes.pro.service.IProFeedbackService;
import com.wei.mes.pro.service.IProRouteProcessService;
import com.wei.system.strategy.AutoCodeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api("生产报工")
@RestController
@RequestMapping(UserConstants.PAD_PATH + "/pro/feedback")
public class ProFeedBackMobController extends BaseController {

    @Autowired
    private IProFeedbackService proFeedbackService;

    @Autowired
    private IMdWorkstationService mdWorkstationService;

    @Autowired
    private IProRouteProcessService proRouteProcessService;

    @Autowired
    private AutoCodeUtil autoCodeUtil;

    /**
     * 新增生产报工记录
     */
    @ApiOperation("新增报工单接口")
    @PreAuthorize("@ss.hasPermi('mes:pro:feedback:add')")
    @Log(title = "生产报工记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ProFeedback proFeedback) {
        MdWorkstation workstation = mdWorkstationService.selectMdWorkstationByWorkstationId(proFeedback.getWorkstationId());
        if (StringUtils.isNotNull(workstation)) {
            proFeedback.setProcessId(workstation.getProcessId());
            proFeedback.setProcessCode(workstation.getProcessCode());
            proFeedback.setProcessName(workstation.getProcessName());
        } else {
            return AjaxResult.error("当前生产任务对应的工作站不存在！");
        }

        // 检查对应的工艺路线和工序配置
        if (StringUtils.isNotNull(proFeedback.getRouteId()) && StringUtils.isNotNull(proFeedback.getProcessId())) {
            ProRouteProcess param = new ProRouteProcess();
            param.setRouteId(proFeedback.getRouteId());
            param.setProcessId(proFeedback.getProcessId());
            List<ProRouteProcess> processes = proRouteProcessService.selectProRouteProcessList(param);
            if (CollectionUtil.isEmpty(processes)) {
                return AjaxResult.error("未找到生产任务对应的工艺工序配置！");
            }
        } else {
            return AjaxResult.error("当前生产任务对应的工艺工序配置无效！");
        }

        // 检查数量
        if (UserConstants.YES.equals(proFeedback.getIsCheck())) {
            if (!StringUtils.isNotNull(proFeedback.getQuantityUncheck())) {
                return AjaxResult.error("当前工作站报工需要经过质检确认，请输入待检测数量!");
            }
        } else {
            if (!StringUtils.isNotNull(proFeedback.getQuantityQualified()) || !StringUtils.isNotNull(proFeedback.getQuantityUnquanlified())) {
                return AjaxResult.error("请输入合格品和不良品数量！");
            }
        }
        String feedbackCode = autoCodeUtil.genSerialCode(UserConstants.FEEDBACK_CODE, "");
        proFeedback.setFeedbackCode(feedbackCode);
        proFeedback.setCreateBy(getUsername());
        proFeedbackService.insertProFeedback(proFeedback);
        return AjaxResult.success(proFeedback);
    }

    /**
     * 查询生产报工记录列表
     */
    @ApiOperation("查询报工单清单-全部")
    @GetMapping("/list")
    public TableDataInfo list(ProFeedback proFeedback) {
        List<ProFeedback> list = proFeedbackService.selectProFeedbackList(proFeedback);
        return getDataTable(list);
    }

    /**
     * 查询生产报工记录列表
     */
    @ApiOperation("查询报工单清单-未审批通过的")
    @GetMapping("/listUnApproved")
    public TableDataInfo listUnApproved(ProFeedback proFeedback) {
        // 不确定前端是否传值，避免漏洞，先重置对象 ---- edit by wei 2024-06-12
        proFeedback = new ProFeedback();
        proFeedback.setStatus(OrderStatusEnum.REFUSE.getCode());
        List<ProFeedback> list = proFeedbackService.selectProFeedbackList(proFeedback);
        return getDataTable(list);
    }


    /**
     * 查询生产报工记录列表
     */
    @ApiOperation("查询报工单清单-已审批通过的")
    @GetMapping("/listApproved")
    public TableDataInfo listApproved(ProFeedback proFeedback) {
        // 不确定前端是否传值，避免漏洞，先重置对象 ---- edit by wei 2024-06-12
        proFeedback = new ProFeedback();
        proFeedback.setStatus(UserConstants.ORDER_STATUS_FINISHED);
        List<ProFeedback> list = proFeedbackService.selectProFeedbackList(proFeedback);
        return getDataTable(list);
    }

    /**
     * 修改生产报工记录
     */
    @ApiOperation("报工修改接口")
    @PreAuthorize("@ss.hasPermi('mes:pro:feedback:edit')")
    @Log(title = "生产报工记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ProFeedback proFeedback) {
        MdWorkstation workstation = mdWorkstationService.selectMdWorkstationByWorkstationId(proFeedback.getWorkstationId());
        if (StringUtils.isNotNull(workstation)) {
            proFeedback.setProcessId(workstation.getProcessId());
            proFeedback.setProcessCode(workstation.getProcessCode());
            proFeedback.setProcessName(workstation.getProcessName());
        } else {
            return AjaxResult.error("当前生产任务对应的工作站不存在！");
        }

        // 检查对应的工艺路线和工序配置
        if (StringUtils.isNotNull(proFeedback.getRouteId()) && StringUtils.isNotNull(proFeedback.getProcessId())) {
            ProRouteProcess param = new ProRouteProcess();
            param.setRouteId(proFeedback.getRouteId());
            param.setProcessId(proFeedback.getProcessId());
            List<ProRouteProcess> processes = proRouteProcessService.selectProRouteProcessList(param);
            if (CollectionUtil.isEmpty(processes)) {
                return AjaxResult.error("未找到生产任务对应的工艺工序配置！");
            }
        } else {
            return AjaxResult.error("当前生产任务对应的工艺工序配置无效！");
        }

        // 检查数量
        if (UserConstants.YES.equals(proFeedback.getIsCheck())) {
            if (!StringUtils.isNotNull(proFeedback.getQuantityUncheck())) {
                return AjaxResult.error("当前工作站报工需要经过质检确认，请输入待检测数量!");
            }
        } else {
            if (!StringUtils.isNotNull(proFeedback.getQuantityQualified()) || !StringUtils.isNotNull(proFeedback.getQuantityUnquanlified())) {
                return AjaxResult.error("请输入合格品和不良品数量！");
            }
        }

        return toAjax(proFeedbackService.updateProFeedback(proFeedback));
    }

    /**
     * 删除生产报工记录
     */
    @ApiOperation("删除报工单")
    @PreAuthorize("@ss.hasPermi('mes:pro:feedback:remove')")
    @Log(title = "生产报工记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{recordIds}")
    public AjaxResult remove(@PathVariable Long[] recordIds) {
        return toAjax(proFeedbackService.deleteProFeedbackByRecordIds(recordIds));
    }
}
