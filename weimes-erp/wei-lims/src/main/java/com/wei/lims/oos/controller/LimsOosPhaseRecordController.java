package com.wei.lims.oos.controller;

import java.util.List;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.lims.oos.domain.LimsOosPhaseRecord;
import com.wei.lims.oos.service.ILimsOosPhaseRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/oos/phase")
public class LimsOosPhaseRecordController extends BaseController {

    @Autowired
    private ILimsOosPhaseRecordService limsOosPhaseRecordService;

    @PreAuthorize("@ss.hasPermi('lims:oos:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsOosPhaseRecord record) {
        startPage();
        List<LimsOosPhaseRecord> list = limsOosPhaseRecordService.selectLimsOosPhaseRecordList(record);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:oos:query')")
    @GetMapping("/list/{investigationId}")
    public AjaxResult listByInvestigation(@PathVariable Long investigationId) {
        return AjaxResult.success(limsOosPhaseRecordService.selectByInvestigationId(investigationId));
    }

    @PreAuthorize("@ss.hasPermi('lims:oos:add')")
    @PostMapping
    public AjaxResult add(@RequestBody LimsOosPhaseRecord record) {
        return toAjax(limsOosPhaseRecordService.insertLimsOosPhaseRecord(record));
    }

    @PreAuthorize("@ss.hasPermi('lims:oos:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody LimsOosPhaseRecord record) {
        return toAjax(limsOosPhaseRecordService.updateLimsOosPhaseRecord(record));
    }
}
