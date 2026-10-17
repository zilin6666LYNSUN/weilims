package com.wei.lims.deviation.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.deviation.domain.LimsDeviationImpact;
import com.wei.lims.deviation.service.ILimsDeviationImpactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/quality/deviation/impact")
public class LimsDeviationImpactController extends BaseController {

    @Autowired
    private ILimsDeviationImpactService limsDeviationImpactService;

    @PreAuthorize("@ss.hasPermi('lims:deviation:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsDeviationImpact impact) {
        startPage();
        List<LimsDeviationImpact> list = limsDeviationImpactService.selectLimsDeviationImpactList(impact);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:deviation:query')")
    @GetMapping("/list/{deviationId}")
    public AjaxResult listByDeviationId(@PathVariable Long deviationId) {
        return AjaxResult.success(limsDeviationImpactService.selectByDeviationId(deviationId));
    }

    @PreAuthorize("@ss.hasPermi('lims:deviation:query')")
    @GetMapping("/{impactId}")
    public AjaxResult getInfo(@PathVariable Long impactId) {
        return AjaxResult.success(limsDeviationImpactService.selectByImpactId(impactId));
    }

    @PreAuthorize("@ss.hasPermi('lims:deviation:add')")
    @Log(title = "偏差影响评估", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsDeviationImpact impact) {
        return toAjax(limsDeviationImpactService.insertLimsDeviationImpact(impact));
    }

    @PreAuthorize("@ss.hasPermi('lims:deviation:edit')")
    @Log(title = "偏差影响评估", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsDeviationImpact impact) {
        return toAjax(limsDeviationImpactService.updateLimsDeviationImpact(impact));
    }

    @PreAuthorize("@ss.hasPermi('lims:deviation:remove')")
    @Log(title = "偏差影响评估", businessType = BusinessType.DELETE)
    @DeleteMapping("/{impactIds}")
    public AjaxResult remove(@PathVariable Long[] impactIds) {
        return toAjax(limsDeviationImpactService.deleteLimsDeviationImpactByIds(impactIds));
    }
}
