package com.wei.lims.supplier.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.supplier.domain.LimsSupplierAudit;
import com.wei.lims.supplier.service.ILimsSupplierAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/supplier/audit")
public class LimsSupplierAuditController extends BaseController {

    @Autowired
    private ILimsSupplierAuditService limssupplierauditService;

    @PreAuthorize("@ss.hasPermi('lims:supplier:audit:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsSupplierAudit limssupplieraudit) {
        startPage();
        List<LimsSupplierAudit> list = limssupplierauditService.selectLimsSupplierAuditList(limssupplieraudit);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:supplier:audit:query')")
    @GetMapping("/{auditId}")
    public AjaxResult getInfo(@PathVariable Long auditId) {
        return AjaxResult.success(limssupplierauditService.selectLimsSupplierAuditByAuditId(auditId));
    }

    @PreAuthorize("@ss.hasPermi('lims:supplier:audit:add')")
    @Log(title = "供应商审计", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsSupplierAudit limssupplieraudit) {
        return toAjax(limssupplierauditService.insertLimsSupplierAudit(limssupplieraudit));
    }

    @PreAuthorize("@ss.hasPermi('lims:supplier:audit:edit')")
    @Log(title = "供应商审计", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsSupplierAudit limssupplieraudit) {
        return toAjax(limssupplierauditService.updateLimsSupplierAudit(limssupplieraudit));
    }

    @PreAuthorize("@ss.hasPermi('lims:supplier:audit:remove')")
    @Log(title = "供应商审计", businessType = BusinessType.DELETE)
    @DeleteMapping("/{auditIds}")
    public AjaxResult remove(@PathVariable Long[] auditIds) {
        return toAjax(limssupplierauditService.deleteLimsSupplierAuditByAuditId(auditIds));
    }
}