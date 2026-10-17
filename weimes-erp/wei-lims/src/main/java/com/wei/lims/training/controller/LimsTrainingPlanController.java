package com.wei.lims.training.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.training.domain.LimsTrainingPlan;
import com.wei.lims.training.service.ILimsTrainingPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/training/plan")
public class LimsTrainingPlanController extends BaseController {

    @Autowired
    private ILimsTrainingPlanService limstrainingplanService;

    @PreAuthorize("@ss.hasPermi('lims:training:plan:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsTrainingPlan limstrainingplan) {
        startPage();
        List<LimsTrainingPlan> list = limstrainingplanService.selectLimsTrainingPlanList(limstrainingplan);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:training:plan:query')")
    @GetMapping("/{planId}")
    public AjaxResult getInfo(@PathVariable Long planId) {
        return AjaxResult.success(limstrainingplanService.selectLimsTrainingPlanByPlanId(planId));
    }

    @PreAuthorize("@ss.hasPermi('lims:training:plan:add')")
    @Log(title = "培训计划", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsTrainingPlan limstrainingplan) {
        return toAjax(limstrainingplanService.insertLimsTrainingPlan(limstrainingplan));
    }

    @PreAuthorize("@ss.hasPermi('lims:training:plan:edit')")
    @Log(title = "培训计划", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsTrainingPlan limstrainingplan) {
        return toAjax(limstrainingplanService.updateLimsTrainingPlan(limstrainingplan));
    }

    @PreAuthorize("@ss.hasPermi('lims:training:plan:remove')")
    @Log(title = "培训计划", businessType = BusinessType.DELETE)
    @DeleteMapping("/{planIds}")
    public AjaxResult remove(@PathVariable Long[] planIds) {
        return toAjax(limstrainingplanService.deleteLimsTrainingPlanByPlanId(planIds));
    }
}