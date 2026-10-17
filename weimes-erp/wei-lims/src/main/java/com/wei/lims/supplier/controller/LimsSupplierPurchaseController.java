package com.wei.lims.supplier.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.supplier.domain.LimsSupplierPurchase;
import com.wei.lims.supplier.service.ILimsSupplierPurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/supplier/purchase")
public class LimsSupplierPurchaseController extends BaseController {

    @Autowired
    private ILimsSupplierPurchaseService limssupplierpurchaseService;

    @PreAuthorize("@ss.hasPermi('lims:supplier:purchase:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsSupplierPurchase limssupplierpurchase) {
        startPage();
        List<LimsSupplierPurchase> list = limssupplierpurchaseService.selectLimsSupplierPurchaseList(limssupplierpurchase);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:supplier:purchase:query')")
    @GetMapping("/{purchaseId}")
    public AjaxResult getInfo(@PathVariable Long purchaseId) {
        return AjaxResult.success(limssupplierpurchaseService.selectLimsSupplierPurchaseByPurchaseId(purchaseId));
    }

    @PreAuthorize("@ss.hasPermi('lims:supplier:purchase:add')")
    @Log(title = "供应商采购记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsSupplierPurchase limssupplierpurchase) {
        return toAjax(limssupplierpurchaseService.insertLimsSupplierPurchase(limssupplierpurchase));
    }

    @PreAuthorize("@ss.hasPermi('lims:supplier:purchase:edit')")
    @Log(title = "供应商采购记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsSupplierPurchase limssupplierpurchase) {
        return toAjax(limssupplierpurchaseService.updateLimsSupplierPurchase(limssupplierpurchase));
    }

    @PreAuthorize("@ss.hasPermi('lims:supplier:purchase:remove')")
    @Log(title = "供应商采购记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{purchaseIds}")
    public AjaxResult remove(@PathVariable Long[] purchaseIds) {
        return toAjax(limssupplierpurchaseService.deleteLimsSupplierPurchaseByPurchaseId(purchaseIds));
    }
}