package com.wei.lims.report.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.report.domain.LimsDataValidationRule;
import com.wei.lims.report.service.ILimsDataValidationRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 数据校验规则Controller
 */
@RestController
@RequestMapping("/lims/report/validation")
public class LimsDataValidationRuleController extends BaseController {

    @Autowired
    private ILimsDataValidationRuleService validationRuleService;

    @PreAuthorize("@ss.hasPermi('lims:report:validation:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsDataValidationRule rule) {
        startPage();
        List<LimsDataValidationRule> list = validationRuleService.selectRuleList(rule);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:report:validation:query')")
    @GetMapping(value = "/{ruleId}")
    public AjaxResult getInfo(@PathVariable("ruleId") Long ruleId) {
        return AjaxResult.success(validationRuleService.selectRuleByRuleId(ruleId));
    }

    @PreAuthorize("@ss.hasPermi('lims:report:validation:add')")
    @Log(title = "数据校验规则", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsDataValidationRule rule) {
        return toAjax(validationRuleService.insertRule(rule));
    }

    @PreAuthorize("@ss.hasPermi('lims:report:validation:edit')")
    @Log(title = "数据校验规则", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsDataValidationRule rule) {
        return toAjax(validationRuleService.updateRule(rule));
    }

    @PreAuthorize("@ss.hasPermi('lims:report:validation:remove')")
    @Log(title = "数据校验规则", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ruleIds}")
    public AjaxResult remove(@PathVariable Long[] ruleIds) {
        return toAjax(validationRuleService.deleteRuleByRuleIds(ruleIds));
    }
}
