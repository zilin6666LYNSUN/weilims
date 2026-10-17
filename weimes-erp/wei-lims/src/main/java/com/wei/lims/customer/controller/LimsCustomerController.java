package com.wei.lims.customer.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.customer.domain.LimsCustomer;
import com.wei.lims.customer.service.ILimsCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/customer")
public class LimsCustomerController extends BaseController {

    @Autowired
    private ILimsCustomerService limscustomerService;

    @PreAuthorize("@ss.hasPermi('lims:customer:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsCustomer limscustomer) {
        startPage();
        List<LimsCustomer> list = limscustomerService.selectLimsCustomerList(limscustomer);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:customer:query')")
    @GetMapping("/{customerId}")
    public AjaxResult getInfo(@PathVariable Long customerId) {
        return AjaxResult.success(limscustomerService.selectLimsCustomerByCustomerId(customerId));
    }

    @PreAuthorize("@ss.hasPermi('lims:customer:add')")
    @Log(title = "客户档案", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsCustomer limscustomer) {
        return toAjax(limscustomerService.insertLimsCustomer(limscustomer));
    }

    @PreAuthorize("@ss.hasPermi('lims:customer:edit')")
    @Log(title = "客户档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsCustomer limscustomer) {
        return toAjax(limscustomerService.updateLimsCustomer(limscustomer));
    }

    @PreAuthorize("@ss.hasPermi('lims:customer:remove')")
    @Log(title = "客户档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{customerIds}")
    public AjaxResult remove(@PathVariable Long[] customerIds) {
        return toAjax(limscustomerService.deleteLimsCustomerByCustomerId(customerIds));
    }
}