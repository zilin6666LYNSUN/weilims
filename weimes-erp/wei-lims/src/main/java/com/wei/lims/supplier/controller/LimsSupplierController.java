package com.wei.lims.supplier.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.supplier.domain.LimsSupplier;
import com.wei.lims.supplier.service.ILimsSupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/supplier")
public class LimsSupplierController extends BaseController {

    @Autowired
    private ILimsSupplierService limssupplierService;

    @PreAuthorize("@ss.hasPermi('lims:supplier:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsSupplier limssupplier) {
        startPage();
        List<LimsSupplier> list = limssupplierService.selectLimsSupplierList(limssupplier);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:supplier:query')")
    @GetMapping("/{vendorId}")
    public AjaxResult getInfo(@PathVariable Long vendorId) {
        return AjaxResult.success(limssupplierService.selectLimsSupplierByVendorId(vendorId));
    }

    @PreAuthorize("@ss.hasPermi('lims:supplier:add')")
    @Log(title = "供应商档案", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsSupplier limssupplier) {
        return toAjax(limssupplierService.insertLimsSupplier(limssupplier));
    }

    @PreAuthorize("@ss.hasPermi('lims:supplier:edit')")
    @Log(title = "供应商档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsSupplier limssupplier) {
        return toAjax(limssupplierService.updateLimsSupplier(limssupplier));
    }

    @PreAuthorize("@ss.hasPermi('lims:supplier:remove')")
    @Log(title = "供应商档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{vendorIds}")
    public AjaxResult remove(@PathVariable Long[] vendorIds) {
        return toAjax(limssupplierService.deleteLimsSupplierByVendorId(vendorIds));
    }
}