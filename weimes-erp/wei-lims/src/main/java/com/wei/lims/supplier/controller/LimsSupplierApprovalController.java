package com.wei.lims.supplier.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.supplier.domain.LimsSupplierApproval;
import com.wei.lims.supplier.service.ILimsSupplierApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/supplier/approval")
public class LimsSupplierApprovalController extends BaseController {

    @Autowired
    private ILimsSupplierApprovalService limssupplierapprovalService;

    @PreAuthorize("@ss.hasPermi('lims:supplier:approval:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsSupplierApproval limssupplierapproval) {
        startPage();
        List<LimsSupplierApproval> list = limssupplierapprovalService.selectLimsSupplierApprovalList(limssupplierapproval);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:supplier:approval:query')")
    @GetMapping("/{approvalId}")
    public AjaxResult getInfo(@PathVariable Long approvalId) {
        return AjaxResult.success(limssupplierapprovalService.selectLimsSupplierApprovalByApprovalId(approvalId));
    }

    @PreAuthorize("@ss.hasPermi('lims:supplier:approval:add')")
    @Log(title = "合格供应商名录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsSupplierApproval limssupplierapproval) {
        return toAjax(limssupplierapprovalService.insertLimsSupplierApproval(limssupplierapproval));
    }

    @PreAuthorize("@ss.hasPermi('lims:supplier:approval:edit')")
    @Log(title = "合格供应商名录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsSupplierApproval limssupplierapproval) {
        return toAjax(limssupplierapprovalService.updateLimsSupplierApproval(limssupplierapproval));
    }

    @PreAuthorize("@ss.hasPermi('lims:supplier:approval:remove')")
    @Log(title = "合格供应商名录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{approvalIds}")
    public AjaxResult remove(@PathVariable Long[] approvalIds) {
        return toAjax(limssupplierapprovalService.deleteLimsSupplierApprovalByApprovalId(approvalIds));
    }
}