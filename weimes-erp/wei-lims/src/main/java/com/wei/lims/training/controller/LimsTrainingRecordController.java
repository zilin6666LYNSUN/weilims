package com.wei.lims.training.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.training.domain.LimsTrainingRecord;
import com.wei.lims.training.service.ILimsTrainingRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/training/record")
public class LimsTrainingRecordController extends BaseController {

    @Autowired
    private ILimsTrainingRecordService limstrainingrecordService;

    @PreAuthorize("@ss.hasPermi('lims:training:record:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsTrainingRecord limstrainingrecord) {
        startPage();
        List<LimsTrainingRecord> list = limstrainingrecordService.selectLimsTrainingRecordList(limstrainingrecord);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:training:record:query')")
    @GetMapping("/{trainingId}")
    public AjaxResult getInfo(@PathVariable Long trainingId) {
        return AjaxResult.success(limstrainingrecordService.selectLimsTrainingRecordByTrainingId(trainingId));
    }

    @PreAuthorize("@ss.hasPermi('lims:training:record:add')")
    @Log(title = "培训记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsTrainingRecord limstrainingrecord) {
        return toAjax(limstrainingrecordService.insertLimsTrainingRecord(limstrainingrecord));
    }

    @PreAuthorize("@ss.hasPermi('lims:training:record:edit')")
    @Log(title = "培训记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsTrainingRecord limstrainingrecord) {
        return toAjax(limstrainingrecordService.updateLimsTrainingRecord(limstrainingrecord));
    }

    @PreAuthorize("@ss.hasPermi('lims:training:record:remove')")
    @Log(title = "培训记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{trainingIds}")
    public AjaxResult remove(@PathVariable Long[] trainingIds) {
        return toAjax(limstrainingrecordService.deleteLimsTrainingRecordByTrainingId(trainingIds));
    }
}