package com.wei.lims.test.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.enums.system.BusinessType;
import com.wei.common.core.page.TableDataInfo;
import com.wei.lims.test.domain.LimsTestResult;
import com.wei.lims.test.service.ILimsTestResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 检验结果Controller
 */
@RestController
@RequestMapping("/lims/test/result")
public class LimsTestResultController extends BaseController {

    @Autowired
    private ILimsTestResultService limsTestResultService;

    /**
     * 查询检验结果列表
     */
    @PreAuthorize("@ss.hasPermi('lims:test:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsTestResult limsTestResult) {
        startPage();
        List<LimsTestResult> list = limsTestResultService.selectLimsTestResultList(limsTestResult);
        return getDataTable(list);
    }

    /**
     * 获取检验结果详情
     */
    @PreAuthorize("@ss.hasPermi('lims:test:order:query')")
    @GetMapping("/{resultId}")
    public AjaxResult getInfo(@PathVariable Long resultId) {
        return AjaxResult.success(limsTestResultService.selectLimsTestResultByResultId(resultId));
    }

    /**
     * 根据检验单查询检验结果
     */
    @PreAuthorize("@ss.hasPermi('lims:test:order:query')")
    @GetMapping("/order/{testOrderId}")
    public AjaxResult listByOrder(@PathVariable Long testOrderId) {
        return AjaxResult.success(limsTestResultService.selectLimsTestResultByTestOrderId(testOrderId));
    }

    /**
     * 录入检验结果
     */
    @PreAuthorize("@ss.hasPermi('lims:test:order:edit')")
    @Log(title = "录入检验结果", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsTestResult limsTestResult) {
        return toAjax(limsTestResultService.insertLimsTestResult(limsTestResult));
    }

    /**
     * 批量录入检验结果
     */
    @PreAuthorize("@ss.hasPermi('lims:test:order:edit')")
    @Log(title = "批量录入检验结果", businessType = BusinessType.INSERT)
    @PostMapping("/batch")
    public AjaxResult batchAdd(@RequestBody List<LimsTestResult> resultList) {
        int rows = 0;
        for (LimsTestResult result : resultList) {
            rows += limsTestResultService.insertLimsTestResult(result);
        }
        return AjaxResult.success(rows);
    }

    /**
     * 修改检验结果
     */
    @PreAuthorize("@ss.hasPermi('lims:test:order:edit')")
    @Log(title = "修改检验结果", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsTestResult limsTestResult) {
        return toAjax(limsTestResultService.updateLimsTestResult(limsTestResult));
    }

    /**
     * 删除检验结果
     */
    @PreAuthorize("@ss.hasPermi('lims:test:order:remove')")
    @Log(title = "删除检验结果", businessType = BusinessType.DELETE)
    @DeleteMapping("/{resultIds}")
    public AjaxResult remove(@PathVariable Long[] resultIds) {
        return toAjax(limsTestResultService.deleteLimsTestResultByResultIds(resultIds));
    }
}
