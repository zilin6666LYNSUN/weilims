package com.wei.lims.change.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.change.domain.LimsChangeImpact;
import com.wei.lims.change.service.ILimsChangeImpactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/quality/change/impact")
public class LimsChangeImpactController extends BaseController {

    @Autowired
    private ILimsChangeImpactService limsChangeImpactService;

    @PreAuthorize("@ss.hasPermi('lims:change:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsChangeImpact impact) {
        startPage();
        List<LimsChangeImpact> list = limsChangeImpactService.selectLimsChangeImpactList(impact);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:change:query')")
    @GetMapping("/list/{changeId}")
    public AjaxResult listByChangeId(@PathVariable Long changeId) {
        return AjaxResult.success(limsChangeImpactService.selectByChangeId(changeId));
    }

    @PreAuthorize("@ss.hasPermi('lims:change:query')")
    @GetMapping("/{impactId}")
    public AjaxResult getInfo(@PathVariable Long impactId) {
        return AjaxResult.success(limsChangeImpactService.selectByImpactId(impactId));
    }

    @PreAuthorize("@ss.hasPermi('lims:change:add')")
    @Log(title = "变更影响评估", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsChangeImpact impact) {
        return toAjax(limsChangeImpactService.insertLimsChangeImpact(impact));
    }

    @PreAuthorize("@ss.hasPermi('lims:change:edit')")
    @Log(title = "变更影响评估", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsChangeImpact impact) {
        return toAjax(limsChangeImpactService.updateLimsChangeImpact(impact));
    }

    @PreAuthorize("@ss.hasPermi('lims:change:remove')")
    @Log(title = "变更影响评估", businessType = BusinessType.DELETE)
    @DeleteMapping("/{impactIds}")
    public AjaxResult remove(@PathVariable Long[] impactIds) {
        return toAjax(limsChangeImpactService.deleteLimsChangeImpactByIds(impactIds));
    }
}
