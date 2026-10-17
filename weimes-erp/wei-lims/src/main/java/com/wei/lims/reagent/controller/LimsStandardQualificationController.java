package com.wei.lims.reagent.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.reagent.domain.LimsStandardQualification;
import com.wei.lims.reagent.service.ILimsStandardQualificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/reagent/standard/qualification")
public class LimsStandardQualificationController extends BaseController {

    @Autowired
    private ILimsStandardQualificationService limsStandardQualificationService;

    @PreAuthorize("@ss.hasPermi('lims:reagent:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsStandardQualification qualification) {
        startPage();
        List<LimsStandardQualification> list = limsStandardQualificationService.selectLimsStandardQualificationList(qualification);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:reagent:query')")
    @GetMapping("/list/{standardId}")
    public AjaxResult listByStandardId(@PathVariable Long standardId) {
        return AjaxResult.success(limsStandardQualificationService.selectByStandardId(standardId));
    }

    @PreAuthorize("@ss.hasPermi('lims:reagent:query')")
    @GetMapping("/{qualId}")
    public AjaxResult getInfo(@PathVariable Long qualId) {
        return AjaxResult.success(limsStandardQualificationService.selectByQualId(qualId));
    }

    @PreAuthorize("@ss.hasPermi('lims:reagent:add')")
    @Log(title = "标准品标定", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsStandardQualification qualification) {
        return toAjax(limsStandardQualificationService.insertLimsStandardQualification(qualification));
    }

    @PreAuthorize("@ss.hasPermi('lims:reagent:edit')")
    @Log(title = "标准品标定", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsStandardQualification qualification) {
        return toAjax(limsStandardQualificationService.updateLimsStandardQualification(qualification));
    }

    @PreAuthorize("@ss.hasPermi('lims:reagent:remove')")
    @Log(title = "标准品标定", businessType = BusinessType.DELETE)
    @DeleteMapping("/{qualIds}")
    public AjaxResult remove(@PathVariable Long[] qualIds) {
        return toAjax(limsStandardQualificationService.deleteLimsStandardQualificationByIds(qualIds));
    }
}
