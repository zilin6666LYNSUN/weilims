package com.wei.lims.training.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.training.domain.LimsStaffTestAuthorization;
import com.wei.lims.training.service.ILimsStaffTestAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/staff/authorization/test")
public class LimsStaffTestAuthorizationController extends BaseController {

    @Autowired
    private ILimsStaffTestAuthorizationService limsstafftestauthorizationService;

    @PreAuthorize("@ss.hasPermi('lims:staff:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsStaffTestAuthorization limsstafftestauthorization) {
        startPage();
        List<LimsStaffTestAuthorization> list = limsstafftestauthorizationService.selectLimsStaffTestAuthorizationList(limsstafftestauthorization);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:staff:query')")
    @GetMapping("/{authId}")
    public AjaxResult getInfo(@PathVariable Long authId) {
        return AjaxResult.success(limsstafftestauthorizationService.selectLimsStaffTestAuthorizationByAuthId(authId));
    }

    @PreAuthorize("@ss.hasPermi('lims:staff:add')")
    @Log(title = "人员检测项授权", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsStaffTestAuthorization limsstafftestauthorization) {
        return toAjax(limsstafftestauthorizationService.insertLimsStaffTestAuthorization(limsstafftestauthorization));
    }

    @PreAuthorize("@ss.hasPermi('lims:staff:edit')")
    @Log(title = "人员检测项授权", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsStaffTestAuthorization limsstafftestauthorization) {
        return toAjax(limsstafftestauthorizationService.updateLimsStaffTestAuthorization(limsstafftestauthorization));
    }

    @PreAuthorize("@ss.hasPermi('lims:staff:remove')")
    @Log(title = "人员检测项授权", businessType = BusinessType.DELETE)
    @DeleteMapping("/{authIds}")
    public AjaxResult remove(@PathVariable Long[] authIds) {
        return toAjax(limsstafftestauthorizationService.deleteLimsStaffTestAuthorizationByAuthId(authIds));
    }
}