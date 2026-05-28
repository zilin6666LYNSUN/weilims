package com.wei.lims.test.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.test.domain.LimsTestMethod;
import com.wei.lims.test.service.ILimsTestMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 检验方法Controller
 */
@RestController
@RequestMapping("/lims/test/method")
public class LimsTestMethodController extends BaseController {

    @Autowired
    private ILimsTestMethodService limsTestMethodService;

    @PreAuthorize("@ss.hasPermi('lims:test:method:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsTestMethod method) {
        startPage();
        List<LimsTestMethod> list = limsTestMethodService.selectLimsTestMethodList(method);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:test:method:query')")
    @GetMapping("/{methodId}")
    public AjaxResult getInfo(@PathVariable Long methodId) {
        return AjaxResult.success(limsTestMethodService.selectLimsTestMethodByMethodId(methodId));
    }

    @PreAuthorize("@ss.hasPermi('lims:test:method:add')")
    @Log(title = "检验方法", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsTestMethod method) {
        return toAjax(limsTestMethodService.insertLimsTestMethod(method));
    }

    @PreAuthorize("@ss.hasPermi('lims:test:method:edit')")
    @Log(title = "检验方法", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsTestMethod method) {
        return toAjax(limsTestMethodService.updateLimsTestMethod(method));
    }

    @PreAuthorize("@ss.hasPermi('lims:test:method:remove')")
    @Log(title = "检验方法", businessType = BusinessType.DELETE)
    @DeleteMapping("/{methodIds}")
    public AjaxResult remove(@PathVariable Long[] methodIds) {
        return toAjax(limsTestMethodService.deleteLimsTestMethodByMethodIds(methodIds));
    }
}
