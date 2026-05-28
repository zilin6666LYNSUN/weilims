package com.wei.lims.sample.controller;

import java.util.List;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.lims.sample.domain.LimsRetainSample;
import com.wei.lims.sample.service.ILimsRetainSampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/sample/retain")
public class LimsRetainSampleController extends BaseController {

    @Autowired
    private ILimsRetainSampleService limsRetainSampleService;

    @PreAuthorize("@ss.hasPermi('lims:sample:query')")
    @GetMapping("/list/{sampleId}")
    public AjaxResult listBySampleId(@PathVariable Long sampleId) {
        return AjaxResult.success(limsRetainSampleService.selectBySampleId(sampleId));
    }

    @PreAuthorize("@ss.hasPermi('lims:sample:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsRetainSample retain) {
        startPage();
        List<LimsRetainSample> list = limsRetainSampleService.selectLimsRetainSampleList(retain);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:sample:add')")
    @PostMapping
    public AjaxResult add(@RequestBody LimsRetainSample retain) {
        return toAjax(limsRetainSampleService.insertLimsRetainSample(retain));
    }

    @PreAuthorize("@ss.hasPermi('lims:sample:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody LimsRetainSample retain) {
        return toAjax(limsRetainSampleService.updateLimsRetainSample(retain));
    }

    @PreAuthorize("@ss.hasPermi('lims:sample:dispose')")
    @PutMapping("/dispose/{retainId}")
    public AjaxResult dispose(@PathVariable Long retainId,
                               @RequestParam String disposeType,
                               @RequestParam String disposeApprover) {
        return toAjax(limsRetainSampleService.disposeRetainSample(retainId, disposeType, disposeApprover));
    }
}
