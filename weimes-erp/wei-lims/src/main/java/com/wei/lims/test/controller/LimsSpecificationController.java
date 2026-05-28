package com.wei.lims.test.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.test.domain.LimsSpecification;
import com.wei.lims.test.domain.LimsSpecificationLine;
import com.wei.lims.test.service.ILimsSpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 质量标准Controller
 */
@RestController
@RequestMapping("/lims/test/spec")
public class LimsSpecificationController extends BaseController {

    @Autowired
    private ILimsSpecificationService limsSpecificationService;

    @PreAuthorize("@ss.hasPermi('lims:test:spec:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsSpecification specification) {
        startPage();
        List<LimsSpecification> list = limsSpecificationService.selectLimsSpecificationList(specification);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:test:spec:query')")
    @GetMapping("/{specificationId}")
    public AjaxResult getInfo(@PathVariable Long specificationId) {
        return AjaxResult.success(limsSpecificationService.selectLimsSpecificationBySpecificationId(specificationId));
    }

    @PreAuthorize("@ss.hasPermi('lims:test:spec:add')")
    @Log(title = "质量标准", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsSpecification specification) {
        return toAjax(limsSpecificationService.insertLimsSpecification(specification));
    }

    @PreAuthorize("@ss.hasPermi('lims:test:spec:edit')")
    @Log(title = "质量标准", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsSpecification specification) {
        return toAjax(limsSpecificationService.updateLimsSpecification(specification));
    }

    @PreAuthorize("@ss.hasPermi('lims:test:spec:remove')")
    @Log(title = "质量标准", businessType = BusinessType.DELETE)
    @DeleteMapping("/{specificationIds}")
    public AjaxResult remove(@PathVariable Long[] specificationIds) {
        return toAjax(limsSpecificationService.deleteLimsSpecificationBySpecificationIds(specificationIds));
    }

    /** 标准行 */
    @PreAuthorize("@ss.hasPermi('lims:test:spec:query')")
    @GetMapping("/line/list/{specificationId}")
    public AjaxResult lineList(@PathVariable Long specificationId) {
        return AjaxResult.success(limsSpecificationService.selectLinesBySpecificationId(specificationId));
    }

    @PreAuthorize("@ss.hasPermi('lims:test:spec:add')")
    @PostMapping("/line")
    public AjaxResult addLine(@RequestBody LimsSpecificationLine line) {
        return toAjax(limsSpecificationService.insertSpecificationLine(line));
    }

    @PreAuthorize("@ss.hasPermi('lims:test:spec:edit')")
    @PutMapping("/line")
    public AjaxResult editLine(@RequestBody LimsSpecificationLine line) {
        return toAjax(limsSpecificationService.updateSpecificationLine(line));
    }

    @PreAuthorize("@ss.hasPermi('lims:test:spec:remove')")
    @DeleteMapping("/line/{lineIds}")
    public AjaxResult removeLine(@PathVariable Long[] lineIds) {
        return toAjax(limsSpecificationService.deleteSpecificationLineByLineIds(lineIds));
    }
}
