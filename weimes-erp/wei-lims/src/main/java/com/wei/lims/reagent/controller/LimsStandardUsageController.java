package com.wei.lims.reagent.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.reagent.domain.LimsStandardUsage;
import com.wei.lims.reagent.service.ILimsStandardUsageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/reagent/standard/usage")
public class LimsStandardUsageController extends BaseController {

    @Autowired
    private ILimsStandardUsageService limsStandardUsageService;

    @PreAuthorize("@ss.hasPermi('lims:reagent:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsStandardUsage usage) {
        startPage();
        List<LimsStandardUsage> list = limsStandardUsageService.selectLimsStandardUsageList(usage);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:reagent:query')")
    @GetMapping("/list/{standardId}")
    public AjaxResult listByStandardId(@PathVariable Long standardId) {
        return AjaxResult.success(limsStandardUsageService.selectByStandardId(standardId));
    }

    @PreAuthorize("@ss.hasPermi('lims:reagent:query')")
    @GetMapping("/{usageId}")
    public AjaxResult getInfo(@PathVariable Long usageId) {
        return AjaxResult.success(limsStandardUsageService.selectByUsageId(usageId));
    }

    @PreAuthorize("@ss.hasPermi('lims:reagent:add')")
    @Log(title = "标准品使用", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsStandardUsage usage) {
        return toAjax(limsStandardUsageService.insertLimsStandardUsage(usage));
    }

    @PreAuthorize("@ss.hasPermi('lims:reagent:edit')")
    @Log(title = "标准品使用", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsStandardUsage usage) {
        return toAjax(limsStandardUsageService.updateLimsStandardUsage(usage));
    }

    @PreAuthorize("@ss.hasPermi('lims:reagent:remove')")
    @Log(title = "标准品使用", businessType = BusinessType.DELETE)
    @DeleteMapping("/{usageIds}")
    public AjaxResult remove(@PathVariable Long[] usageIds) {
        return toAjax(limsStandardUsageService.deleteLimsStandardUsageByIds(usageIds));
    }
}
