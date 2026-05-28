package com.wei.lims.sample.controller;

import java.util.List;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.lims.sample.domain.LimsSampleDispose;
import com.wei.lims.sample.service.ILimsSampleDisposeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/sample/dispose")
public class LimsSampleDisposeController extends BaseController {

    @Autowired
    private ILimsSampleDisposeService limsSampleDisposeService;

    @PreAuthorize("@ss.hasPermi('lims:sample:query')")
    @GetMapping("/list/{sampleId}")
    public AjaxResult listBySampleId(@PathVariable Long sampleId) {
        return AjaxResult.success(limsSampleDisposeService.selectBySampleId(sampleId));
    }

    @PreAuthorize("@ss.hasPermi('lims:sample:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsSampleDispose dispose) {
        startPage();
        List<LimsSampleDispose> list = limsSampleDisposeService.selectLimsSampleDisposeList(dispose);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:sample:add')")
    @PostMapping
    public AjaxResult add(@RequestBody LimsSampleDispose dispose) {
        return toAjax(limsSampleDisposeService.insertLimsSampleDispose(dispose));
    }

    @PreAuthorize("@ss.hasPermi('lims:sample:approve')")
    @PutMapping("/approve/{disposeId}")
    public AjaxResult approve(@PathVariable Long disposeId, @RequestParam Long approvedBy) {
        return toAjax(limsSampleDisposeService.approveDispose(disposeId, approvedBy));
    }
}
