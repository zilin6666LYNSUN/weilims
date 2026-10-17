package com.wei.lims.training.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.training.domain.LimsQualification;
import com.wei.lims.training.service.ILimsQualificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/qualification")
public class LimsQualificationController extends BaseController {

    @Autowired
    private ILimsQualificationService limsqualificationService;

    @PreAuthorize("@ss.hasPermi('lims:qualification:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsQualification limsqualification) {
        startPage();
        List<LimsQualification> list = limsqualificationService.selectLimsQualificationList(limsqualification);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:qualification:query')")
    @GetMapping("/{qualificationId}")
    public AjaxResult getInfo(@PathVariable Long qualificationId) {
        return AjaxResult.success(limsqualificationService.selectLimsQualificationByQualificationId(qualificationId));
    }

    @PreAuthorize("@ss.hasPermi('lims:qualification:add')")
    @Log(title = "人员资质", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsQualification limsqualification) {
        return toAjax(limsqualificationService.insertLimsQualification(limsqualification));
    }

    @PreAuthorize("@ss.hasPermi('lims:qualification:edit')")
    @Log(title = "人员资质", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsQualification limsqualification) {
        return toAjax(limsqualificationService.updateLimsQualification(limsqualification));
    }

    @PreAuthorize("@ss.hasPermi('lims:qualification:remove')")
    @Log(title = "人员资质", businessType = BusinessType.DELETE)
    @DeleteMapping("/{qualificationIds}")
    public AjaxResult remove(@PathVariable Long[] qualificationIds) {
        return toAjax(limsqualificationService.deleteLimsQualificationByQualificationId(qualificationIds));
    }

    @PreAuthorize("@ss.hasPermi('lims:qualification:expire')")
    @GetMapping("/expiring")
    public TableDataInfo expiring() {
        return getDataTable(limsqualificationService.selectExpiringQualifications());
    }
}