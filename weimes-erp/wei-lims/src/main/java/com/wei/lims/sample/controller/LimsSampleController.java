package com.wei.lims.sample.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.constant.UserConstants;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.common.utils.poi.ExcelUtil;
import com.wei.lims.sample.domain.LimsSample;
import com.wei.lims.sample.service.ILimsSampleService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 样品管理Controller
 */
@RestController
@RequestMapping("/lims/sample")
public class LimsSampleController extends BaseController {

    @Autowired
    private ILimsSampleService limsSampleService;

    @PreAuthorize("@ss.hasPermi('lims:sample:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsSample limsSample) {
        startPage();
        List<LimsSample> list = limsSampleService.selectLimsSampleList(limsSample);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:sample:export')")
    @Log(title = "样品管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LimsSample limsSample) {
        List<LimsSample> list = limsSampleService.selectLimsSampleList(limsSample);
        ExcelUtil<LimsSample> util = new ExcelUtil<LimsSample>(LimsSample.class);
        util.exportExcel(response, list, "样品数据");
    }

    @PreAuthorize("@ss.hasPermi('lims:sample:query')")
    @GetMapping(value = "/{sampleId}")
    public AjaxResult getInfo(@PathVariable("sampleId") Long sampleId) {
        return AjaxResult.success(limsSampleService.selectLimsSampleBySampleId(sampleId));
    }

    @PreAuthorize("@ss.hasPermi('lims:sample:add')")
    @Log(title = "样品管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsSample limsSample) {
        if (UserConstants.NOT_UNIQUE.equals(limsSampleService.checkSampleCodeUnique(limsSample))) {
            return AjaxResult.error("样品编号已存在！");
        }
        return toAjax(limsSampleService.insertLimsSample(limsSample));
    }

    @PreAuthorize("@ss.hasPermi('lims:sample:edit')")
    @Log(title = "样品管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsSample limsSample) {
        if (UserConstants.NOT_UNIQUE.equals(limsSampleService.checkSampleCodeUnique(limsSample))) {
            return AjaxResult.error("样品编号已存在！");
        }
        return toAjax(limsSampleService.updateLimsSample(limsSample));
    }

    @PreAuthorize("@ss.hasPermi('lims:sample:remove')")
    @Log(title = "样品管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{sampleIds}")
    public AjaxResult remove(@PathVariable Long[] sampleIds) {
        return toAjax(limsSampleService.deleteLimsSampleBySampleIds(sampleIds));
    }

    /**
     * 样品接收
     */
    @PreAuthorize("@ss.hasPermi('lims:sample:receive')")
    @Log(title = "样品接收", businessType = BusinessType.UPDATE)
    @PutMapping("/receive/{sampleId}")
    public AjaxResult receive(@PathVariable Long sampleId) {
        return toAjax(limsSampleService.receiveSample(sampleId));
    }

    /**
     * 样品状态变更
     */
    @PreAuthorize("@ss.hasPermi('lims:sample:edit')")
    @Log(title = "样品状态变更", businessType = BusinessType.UPDATE)
    @PutMapping("/status/{sampleId}/{toStatus}")
    public AjaxResult changeStatus(@PathVariable Long sampleId, @PathVariable String toStatus,
                                   @RequestParam(required = false) String reason) {
        return toAjax(limsSampleService.changeSampleStatus(sampleId, toStatus, reason));
    }
}
