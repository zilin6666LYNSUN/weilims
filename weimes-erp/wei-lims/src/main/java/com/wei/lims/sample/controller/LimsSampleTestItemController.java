package com.wei.lims.sample.controller;

import java.util.List;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.lims.sample.domain.LimsSampleTestItem;
import com.wei.lims.sample.service.ILimsSampleTestItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 样品-检测项关联Controller
 */
@RestController
@RequestMapping("/lims/sample/testitem")
public class LimsSampleTestItemController extends BaseController {

    @Autowired
    private ILimsSampleTestItemService limsSampleTestItemService;

    @PreAuthorize("@ss.hasPermi('lims:sample:query')")
    @GetMapping("/list/{sampleId}")
    public AjaxResult listBySampleId(@PathVariable Long sampleId) {
        return AjaxResult.success(limsSampleTestItemService.selectBySampleId(sampleId));
    }

    @PreAuthorize("@ss.hasPermi('lims:sample:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsSampleTestItem item) {
        startPage();
        List<LimsSampleTestItem> list = limsSampleTestItemService.selectLimsSampleTestItemList(item);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:sample:add')")
    @PostMapping
    public AjaxResult add(@RequestBody LimsSampleTestItem item) {
        return toAjax(limsSampleTestItemService.insertLimsSampleTestItem(item));
    }

    @PreAuthorize("@ss.hasPermi('lims:sample:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody LimsSampleTestItem item) {
        return toAjax(limsSampleTestItemService.updateLimsSampleTestItem(item));
    }

    @PreAuthorize("@ss.hasPermi('lims:sample:remove')")
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id) {
        return toAjax(limsSampleTestItemService.deleteById(id));
    }
}
