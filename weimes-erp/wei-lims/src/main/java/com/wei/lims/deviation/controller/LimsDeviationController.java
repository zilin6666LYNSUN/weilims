package com.wei.lims.deviation.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.deviation.domain.LimsDeviation;
import com.wei.lims.deviation.service.ILimsDeviationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/deviation")
public class LimsDeviationController extends BaseController {

    @Autowired
    private ILimsDeviationService limsDeviationService;

    @PreAuthorize("@ss.hasPermi('lims:deviation:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsDeviation deviation) {
        startPage();
        List<LimsDeviation> list = limsDeviationService.selectLimsDeviationList(deviation);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:deviation:query')")
    @GetMapping("/{deviationId}")
    public AjaxResult getInfo(@PathVariable Long deviationId) {
        return AjaxResult.success(limsDeviationService.selectByDeviationId(deviationId));
    }

    @PreAuthorize("@ss.hasPermi('lims:deviation:add')")
    @Log(title = "偏差管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsDeviation deviation) {
        return toAjax(limsDeviationService.insertLimsDeviation(deviation));
    }

    @PreAuthorize("@ss.hasPermi('lims:deviation:edit')")
    @Log(title = "偏差管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsDeviation deviation) {
        return toAjax(limsDeviationService.updateLimsDeviation(deviation));
    }

    @PreAuthorize("@ss.hasPermi('lims:deviation:remove')")
    @Log(title = "偏差管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deviationIds}")
    public AjaxResult remove(@PathVariable Long[] deviationIds) {
        return toAjax(limsDeviationService.deleteLimsDeviationByIds(deviationIds));
    }

    @PreAuthorize("@ss.hasPermi('lims:deviation:edit')")
    @PutMapping("/submit/{deviationId}")
    public AjaxResult submit(@PathVariable Long deviationId) {
        return toAjax(limsDeviationService.submitDeviation(deviationId));
    }

    @PreAuthorize("@ss.hasPermi('lims:deviation:approve')")
    @PutMapping("/approve/{deviationId}")
    public AjaxResult approve(@PathVariable Long deviationId) {
        return toAjax(limsDeviationService.approveDeviation(deviationId));
    }

    @PreAuthorize("@ss.hasPermi('lims:deviation:close')")
    @PutMapping("/close/{deviationId}")
    public AjaxResult close(@PathVariable Long deviationId) {
        return toAjax(limsDeviationService.closeDeviation(deviationId));
    }
}
