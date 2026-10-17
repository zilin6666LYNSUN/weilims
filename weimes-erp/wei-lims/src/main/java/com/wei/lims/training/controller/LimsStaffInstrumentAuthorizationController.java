package com.wei.lims.training.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.training.domain.LimsStaffInstrumentAuthorization;
import com.wei.lims.training.service.ILimsStaffInstrumentAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/staff/authorization/instrument")
public class LimsStaffInstrumentAuthorizationController extends BaseController {

    @Autowired
    private ILimsStaffInstrumentAuthorizationService limsstaffinstrumentauthorizationService;

    @PreAuthorize("@ss.hasPermi('lims:staff:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsStaffInstrumentAuthorization limsstaffinstrumentauthorization) {
        startPage();
        List<LimsStaffInstrumentAuthorization> list = limsstaffinstrumentauthorizationService.selectLimsStaffInstrumentAuthorizationList(limsstaffinstrumentauthorization);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:staff:query')")
    @GetMapping("/{authId}")
    public AjaxResult getInfo(@PathVariable Long authId) {
        return AjaxResult.success(limsstaffinstrumentauthorizationService.selectLimsStaffInstrumentAuthorizationByAuthId(authId));
    }

    @PreAuthorize("@ss.hasPermi('lims:staff:add')")
    @Log(title = "人员仪器授权", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsStaffInstrumentAuthorization limsstaffinstrumentauthorization) {
        return toAjax(limsstaffinstrumentauthorizationService.insertLimsStaffInstrumentAuthorization(limsstaffinstrumentauthorization));
    }

    @PreAuthorize("@ss.hasPermi('lims:staff:edit')")
    @Log(title = "人员仪器授权", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsStaffInstrumentAuthorization limsstaffinstrumentauthorization) {
        return toAjax(limsstaffinstrumentauthorizationService.updateLimsStaffInstrumentAuthorization(limsstaffinstrumentauthorization));
    }

    @PreAuthorize("@ss.hasPermi('lims:staff:remove')")
    @Log(title = "人员仪器授权", businessType = BusinessType.DELETE)
    @DeleteMapping("/{authIds}")
    public AjaxResult remove(@PathVariable Long[] authIds) {
        return toAjax(limsstaffinstrumentauthorizationService.deleteLimsStaffInstrumentAuthorizationByAuthId(authIds));
    }
}