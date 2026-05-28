package com.wei.lims.reagent.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.reagent.domain.LimsCultureCollection;
import com.wei.lims.reagent.service.ILimsCultureCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/reagent/culture")
public class LimsCultureCollectionController extends BaseController {

    @Autowired
    private ILimsCultureCollectionService limsCultureCollectionService;

    @PreAuthorize("@ss.hasPermi('lims:reagent:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsCultureCollection culture) {
        startPage();
        List<LimsCultureCollection> list = limsCultureCollectionService.selectLimsCultureCollectionList(culture);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:reagent:query')")
    @GetMapping("/{cultureId}")
    public AjaxResult getInfo(@PathVariable Long cultureId) {
        return AjaxResult.success(limsCultureCollectionService.selectByCultureId(cultureId));
    }

    @PreAuthorize("@ss.hasPermi('lims:reagent:add')")
    @Log(title = "菌种管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsCultureCollection culture) {
        return toAjax(limsCultureCollectionService.insertLimsCultureCollection(culture));
    }

    @PreAuthorize("@ss.hasPermi('lims:reagent:edit')")
    @Log(title = "菌种管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsCultureCollection culture) {
        return toAjax(limsCultureCollectionService.updateLimsCultureCollection(culture));
    }

    @PreAuthorize("@ss.hasPermi('lims:reagent:remove')")
    @Log(title = "菌种管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{cultureIds}")
    public AjaxResult remove(@PathVariable Long[] cultureIds) {
        return toAjax(limsCultureCollectionService.deleteLimsCultureCollectionByIds(cultureIds));
    }
}
