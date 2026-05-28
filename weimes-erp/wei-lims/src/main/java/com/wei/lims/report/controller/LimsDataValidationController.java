package com.wei.lims.report.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.report.domain.LimsDataValidationLog;
import com.wei.lims.report.service.ILimsDataValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 数据校验执行Controller
 */
@RestController
@RequestMapping("/lims/report/validation/exec")
public class LimsDataValidationController extends BaseController {

    @Autowired
    private ILimsDataValidationService validationService;

    /**
     * 执行数据校验
     */
    @PreAuthorize("@ss.hasPermi('lims:report:validation:execute')")
    @Log(title = "执行数据校验", businessType = BusinessType.OTHER)
    @PostMapping("/validate/{testOrderId}")
    public AjaxResult validate(@PathVariable Long testOrderId) {
        List<LimsDataValidationLog> logs = validationService.validateTestOrder(testOrderId);
        boolean allPassed = validationService.isAllValidationPassed(testOrderId);
        AjaxResult result = AjaxResult.success(logs);
        result.put("allPassed", allPassed);
        return result;
    }

    /**
     * 查询校验日志
     */
    @PreAuthorize("@ss.hasPermi('lims:report:validation:query')")
    @GetMapping("/logs/{testOrderId}")
    public AjaxResult logs(@PathVariable Long testOrderId) {
        return AjaxResult.success(validationService.selectValidationLogs(testOrderId));
    }

    /**
     * 检查是否全部通过
     */
    @PreAuthorize("@ss.hasPermi('lims:report:validation:query')")
    @GetMapping("/check/{testOrderId}")
    public AjaxResult check(@PathVariable Long testOrderId) {
        return AjaxResult.success(validationService.isAllValidationPassed(testOrderId));
    }
}
