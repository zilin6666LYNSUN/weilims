package com.wei.lims.oos.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.oos.domain.LimsOosInvestigation;
import com.wei.lims.oos.service.ILimsOosInvestigationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/oos/investigation")
public class LimsOosInvestigationController extends BaseController {

    @Autowired
    private ILimsOosInvestigationService limsOosInvestigationService;

    @PreAuthorize("@ss.hasPermi('lims:oos:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsOosInvestigation investigation) {
        startPage();
        List<LimsOosInvestigation> list = limsOosInvestigationService.selectLimsOosInvestigationList(investigation);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:oos:query')")
    @GetMapping("/{investigationId}")
    public AjaxResult getInfo(@PathVariable Long investigationId) {
        return AjaxResult.success(limsOosInvestigationService.selectByInvestigationId(investigationId));
    }

    @PreAuthorize("@ss.hasPermi('lims:oos:add')")
    @Log(title = "OOS/OOT调查", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsOosInvestigation investigation) {
        return toAjax(limsOosInvestigationService.insertLimsOosInvestigation(investigation));
    }

    @PreAuthorize("@ss.hasPermi('lims:oos:edit')")
    @Log(title = "OOS/OOT调查", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsOosInvestigation investigation) {
        return toAjax(limsOosInvestigationService.updateLimsOosInvestigation(investigation));
    }

    @PreAuthorize("@ss.hasPermi('lims:oos:remove')")
    @Log(title = "OOS/OOT调查", businessType = BusinessType.DELETE)
    @DeleteMapping("/{investigationIds}")
    public AjaxResult remove(@PathVariable Long[] investigationIds) {
        return toAjax(limsOosInvestigationService.deleteLimsOosInvestigationByIds(investigationIds));
    }

    @PreAuthorize("@ss.hasPermi('lims:oos:edit')")
    @Log(title = "OOS/OOT调查-提交", businessType = BusinessType.UPDATE)
    @PutMapping("/submit/{investigationId}")
    public AjaxResult submit(@PathVariable Long investigationId) {
        return toAjax(limsOosInvestigationService.submitInvestigation(investigationId));
    }

    @PreAuthorize("@ss.hasPermi('lims:oos:review')")
    @Log(title = "OOS/OOT调查-复核", businessType = BusinessType.UPDATE)
    @PutMapping("/review/{investigationId}")
    public AjaxResult review(@PathVariable Long investigationId) {
        return toAjax(limsOosInvestigationService.reviewInvestigation(investigationId));
    }

    @PreAuthorize("@ss.hasPermi('lims:oos:approve')")
    @Log(title = "OOS/OOT调查-批准", businessType = BusinessType.UPDATE)
    @PutMapping("/approve/{investigationId}")
    public AjaxResult approve(@PathVariable Long investigationId) {
        return toAjax(limsOosInvestigationService.approveInvestigation(investigationId));
    }
}
