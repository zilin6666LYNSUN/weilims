package com.wei.lims.reagent.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.reagent.domain.LimsReferenceStandard;
import com.wei.lims.reagent.service.ILimsReferenceStandardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/reagent/standard")
public class LimsReferenceStandardController extends BaseController {

    @Autowired
    private ILimsReferenceStandardService limsReferenceStandardService;

    @PreAuthorize("@ss.hasPermi('lims:reagent:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsReferenceStandard standard) {
        startPage();
        List<LimsReferenceStandard> list = limsReferenceStandardService.selectLimsReferenceStandardList(standard);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:reagent:query')")
    @GetMapping("/{standardId}")
    public AjaxResult getInfo(@PathVariable Long standardId) {
        return AjaxResult.success(limsReferenceStandardService.selectByStandardId(standardId));
    }

    @PreAuthorize("@ss.hasPermi('lims:reagent:add')")
    @Log(title = "标准品管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsReferenceStandard standard) {
        return toAjax(limsReferenceStandardService.insertLimsReferenceStandard(standard));
    }

    @PreAuthorize("@ss.hasPermi('lims:reagent:edit')")
    @Log(title = "标准品管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsReferenceStandard standard) {
        return toAjax(limsReferenceStandardService.updateLimsReferenceStandard(standard));
    }

    @PreAuthorize("@ss.hasPermi('lims:reagent:remove')")
    @Log(title = "标准品管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{standardIds}")
    public AjaxResult remove(@PathVariable Long[] standardIds) {
        return toAjax(limsReferenceStandardService.deleteLimsReferenceStandardByIds(standardIds));
    }
}
