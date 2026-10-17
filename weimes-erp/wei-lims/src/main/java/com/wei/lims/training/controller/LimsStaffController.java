package com.wei.lims.training.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.training.domain.LimsStaff;
import com.wei.lims.training.service.ILimsStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/staff")
public class LimsStaffController extends BaseController {

    @Autowired
    private ILimsStaffService limsstaffService;

    @PreAuthorize("@ss.hasPermi('lims:staff:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsStaff limsstaff) {
        startPage();
        List<LimsStaff> list = limsstaffService.selectLimsStaffList(limsstaff);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:staff:query')")
    @GetMapping("/{staffId}")
    public AjaxResult getInfo(@PathVariable Long staffId) {
        return AjaxResult.success(limsstaffService.selectLimsStaffByStaffId(staffId));
    }

    @PreAuthorize("@ss.hasPermi('lims:staff:add')")
    @Log(title = "人员档案", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsStaff limsstaff) {
        return toAjax(limsstaffService.insertLimsStaff(limsstaff));
    }

    @PreAuthorize("@ss.hasPermi('lims:staff:edit')")
    @Log(title = "人员档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsStaff limsstaff) {
        return toAjax(limsstaffService.updateLimsStaff(limsstaff));
    }

    @PreAuthorize("@ss.hasPermi('lims:staff:remove')")
    @Log(title = "人员档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{staffIds}")
    public AjaxResult remove(@PathVariable Long[] staffIds) {
        return toAjax(limsstaffService.deleteLimsStaffByStaffId(staffIds));
    }
}