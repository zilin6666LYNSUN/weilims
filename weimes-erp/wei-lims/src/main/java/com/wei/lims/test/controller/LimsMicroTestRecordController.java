package com.wei.lims.test.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.test.domain.LimsMicroTestRecord;
import com.wei.lims.test.service.ILimsMicroTestRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/test/micro")
public class LimsMicroTestRecordController extends BaseController {

    @Autowired
    private ILimsMicroTestRecordService limsMicroTestRecordService;

    @PreAuthorize("@ss.hasPermi('lims:test:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsMicroTestRecord record) {
        startPage();
        List<LimsMicroTestRecord> list = limsMicroTestRecordService.selectLimsMicroTestRecordList(record);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:test:query')")
    @GetMapping("/list/order/{testOrderId}")
    public AjaxResult listByTestOrderId(@PathVariable Long testOrderId) {
        return AjaxResult.success(limsMicroTestRecordService.selectByTestOrderId(testOrderId));
    }

    @PreAuthorize("@ss.hasPermi('lims:test:query')")
    @GetMapping("/{recordId}")
    public AjaxResult getInfo(@PathVariable Long recordId) {
        return AjaxResult.success(limsMicroTestRecordService.selectByRecordId(recordId));
    }

    @PreAuthorize("@ss.hasPermi('lims:test:add')")
    @Log(title = "微生物检测记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsMicroTestRecord record) {
        return toAjax(limsMicroTestRecordService.insertLimsMicroTestRecord(record));
    }

    @PreAuthorize("@ss.hasPermi('lims:test:edit')")
    @Log(title = "微生物检测记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsMicroTestRecord record) {
        return toAjax(limsMicroTestRecordService.updateLimsMicroTestRecord(record));
    }

    /** 录入结果并自动判定 */
    @PreAuthorize("@ss.hasPermi('lims:test:edit')")
    @Log(title = "微生物检测记录", businessType = BusinessType.UPDATE)
    @PutMapping("/result")
    public AjaxResult recordResult(@RequestBody LimsMicroTestRecord record) {
        return toAjax(limsMicroTestRecordService.recordResult(record));
    }

    @PreAuthorize("@ss.hasPermi('lims:test:remove')")
    @Log(title = "微生物检测记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{recordIds}")
    public AjaxResult remove(@PathVariable Long[] recordIds) {
        return toAjax(limsMicroTestRecordService.deleteLimsMicroTestRecordByRecordIds(recordIds));
    }
}
