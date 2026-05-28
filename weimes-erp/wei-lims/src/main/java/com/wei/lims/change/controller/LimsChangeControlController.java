package com.wei.lims.change.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.change.domain.LimsChangeControl;
import com.wei.lims.change.service.ILimsChangeControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/change/control")
public class LimsChangeControlController extends BaseController {

    @Autowired
    private ILimsChangeControlService limsChangeControlService;

    @PreAuthorize("@ss.hasPermi('lims:change:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsChangeControl changeControl) {
        startPage();
        List<LimsChangeControl> list = limsChangeControlService.selectLimsChangeControlList(changeControl);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:change:query')")
    @GetMapping("/{changeId}")
    public AjaxResult getInfo(@PathVariable Long changeId) {
        return AjaxResult.success(limsChangeControlService.selectByChangeId(changeId));
    }

    @PreAuthorize("@ss.hasPermi('lims:change:add')")
    @Log(title = "变更控制", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsChangeControl changeControl) {
        return toAjax(limsChangeControlService.insertLimsChangeControl(changeControl));
    }

    @PreAuthorize("@ss.hasPermi('lims:change:edit')")
    @Log(title = "变更控制", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsChangeControl changeControl) {
        return toAjax(limsChangeControlService.updateLimsChangeControl(changeControl));
    }

    @PreAuthorize("@ss.hasPermi('lims:change:remove')")
    @Log(title = "变更控制", businessType = BusinessType.DELETE)
    @DeleteMapping("/{changeIds}")
    public AjaxResult remove(@PathVariable Long[] changeIds) {
        return toAjax(limsChangeControlService.deleteLimsChangeControlByIds(changeIds));
    }

    @PreAuthorize("@ss.hasPermi('lims:change:edit')")
    @PutMapping("/submit/{changeId}")
    public AjaxResult submit(@PathVariable Long changeId) {
        return toAjax(limsChangeControlService.submitChange(changeId));
    }

    @PreAuthorize("@ss.hasPermi('lims:change:approve')")
    @PutMapping("/approve/{changeId}")
    public AjaxResult approve(@PathVariable Long changeId) {
        return toAjax(limsChangeControlService.approveChange(changeId));
    }

    @PreAuthorize("@ss.hasPermi('lims:change:implement')")
    @PutMapping("/implement/{changeId}")
    public AjaxResult implement(@PathVariable Long changeId) {
        return toAjax(limsChangeControlService.implementChange(changeId));
    }

    @PreAuthorize("@ss.hasPermi('lims:change:complete')")
    @PutMapping("/complete/{changeId}")
    public AjaxResult complete(@PathVariable Long changeId) {
        return toAjax(limsChangeControlService.completeChange(changeId));
    }
}
