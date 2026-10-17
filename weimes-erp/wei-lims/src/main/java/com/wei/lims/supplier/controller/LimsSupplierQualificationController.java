package com.wei.lims.supplier.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.supplier.domain.LimsSupplierQualification;
import com.wei.lims.supplier.service.ILimsSupplierQualificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/supplier/qualification")
public class LimsSupplierQualificationController extends BaseController {

    @Autowired
    private ILimsSupplierQualificationService limssupplierqualificationService;

    @PreAuthorize("@ss.hasPermi('lims:supplier:qualification:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsSupplierQualification limssupplierqualification) {
        startPage();
        List<LimsSupplierQualification> list = limssupplierqualificationService.selectLimsSupplierQualificationList(limssupplierqualification);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:supplier:qualification:query')")
    @GetMapping("/{qualificationId}")
    public AjaxResult getInfo(@PathVariable Long qualificationId) {
        return AjaxResult.success(limssupplierqualificationService.selectLimsSupplierQualificationByQualificationId(qualificationId));
    }

    @PreAuthorize("@ss.hasPermi('lims:supplier:qualification:add')")
    @Log(title = "供应商资质", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsSupplierQualification limssupplierqualification) {
        return toAjax(limssupplierqualificationService.insertLimsSupplierQualification(limssupplierqualification));
    }

    @PreAuthorize("@ss.hasPermi('lims:supplier:qualification:edit')")
    @Log(title = "供应商资质", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsSupplierQualification limssupplierqualification) {
        return toAjax(limssupplierqualificationService.updateLimsSupplierQualification(limssupplierqualification));
    }

    @PreAuthorize("@ss.hasPermi('lims:supplier:qualification:remove')")
    @Log(title = "供应商资质", businessType = BusinessType.DELETE)
    @DeleteMapping("/{qualificationIds}")
    public AjaxResult remove(@PathVariable Long[] qualificationIds) {
        return toAjax(limssupplierqualificationService.deleteLimsSupplierQualificationByQualificationId(qualificationIds));
    }
}