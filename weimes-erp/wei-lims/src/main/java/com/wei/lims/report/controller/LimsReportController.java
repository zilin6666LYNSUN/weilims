package com.wei.lims.report.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.constant.UserConstants;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.common.utils.poi.ExcelUtil;
import com.wei.lims.report.domain.LimsReport;
import com.wei.lims.report.domain.LimsReportSignature;
import com.wei.lims.report.mapper.LimsReportSignatureMapper;
import com.wei.lims.report.service.ILimsReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 检验报告Controller
 * GMP合规：编制-复核-批准-放行 四步审批流程
 */
@RestController
@RequestMapping("/lims/report/test")
public class LimsReportController extends BaseController {

    @Autowired
    private ILimsReportService limsReportService;

    @Autowired
    private LimsReportSignatureMapper limsReportSignatureMapper;

    @PreAuthorize("@ss.hasPermi('lims:report:test:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsReport limsReport) {
        startPage();
        List<LimsReport> list = limsReportService.selectLimsReportList(limsReport);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:report:test:export')")
    @Log(title = "检验报告", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LimsReport limsReport) {
        List<LimsReport> list = limsReportService.selectLimsReportList(limsReport);
        ExcelUtil<LimsReport> util = new ExcelUtil<LimsReport>(LimsReport.class);
        util.exportExcel(response, list, "检验报告数据");
    }

    @PreAuthorize("@ss.hasPermi('lims:report:test:query')")
    @GetMapping(value = "/{reportId}")
    public AjaxResult getInfo(@PathVariable("reportId") Long reportId) {
        return AjaxResult.success(limsReportService.selectLimsReportByReportId(reportId));
    }

    @PreAuthorize("@ss.hasPermi('lims:report:test:add')")
    @Log(title = "检验报告", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsReport limsReport) {
        if ("1".equals(limsReportService.checkReportCodeUnique(limsReport.getReportCode()))) {
            return AjaxResult.error("报告编号已存在！");
        }
        return toAjax(limsReportService.insertLimsReport(limsReport));
    }

    @PreAuthorize("@ss.hasPermi('lims:report:test:edit')")
    @Log(title = "检验报告", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsReport limsReport) {
        return toAjax(limsReportService.updateLimsReport(limsReport));
    }

    @PreAuthorize("@ss.hasPermi('lims:report:test:remove')")
    @Log(title = "检验报告", businessType = BusinessType.DELETE)
    @DeleteMapping("/{reportIds}")
    public AjaxResult remove(@PathVariable Long[] reportIds) {
        return toAjax(limsReportService.deleteLimsReportByReportIds(reportIds));
    }

    /**
     * 从检验订单创建报告
     */
    @PreAuthorize("@ss.hasPermi('lims:report:test:add')")
    @Log(title = "创建检验报告", businessType = BusinessType.INSERT)
    @PostMapping("/createFromOrder")
    public AjaxResult createFromOrder(@RequestParam Long testOrderId,
                                      @RequestParam Long templateId,
                                      @RequestParam String reportType) {
        LimsReport report = limsReportService.createReportFromTestOrder(testOrderId, templateId, reportType);
        return AjaxResult.success(report);
    }

    /**
     * 生成报告数据
     */
    @PreAuthorize("@ss.hasPermi('lims:report:test:generate')")
    @Log(title = "生成报告", businessType = BusinessType.UPDATE)
    @PutMapping("/generate/{reportId}")
    public AjaxResult generate(@PathVariable Long reportId) {
        return toAjax(limsReportService.generateReport(reportId));
    }

    /**
     * 报告复核（GMP四眼原则）
     */
    @PreAuthorize("@ss.hasPermi('lims:report:test:review')")
    @Log(title = "报告复核", businessType = BusinessType.UPDATE)
    @PutMapping("/review/{reportId}")
    public AjaxResult review(@PathVariable Long reportId,
                              @RequestParam String reviewResult,
                              @RequestParam(required = false) String comment) {
        return toAjax(limsReportService.reviewReport(reportId, reviewResult, comment));
    }

    /**
     * 报告批准（GMP四眼原则）
     */
    @PreAuthorize("@ss.hasPermi('lims:report:test:approve')")
    @Log(title = "报告批准", businessType = BusinessType.UPDATE)
    @PutMapping("/approve/{reportId}")
    public AjaxResult approve(@PathVariable Long reportId,
                              @RequestParam String approveResult,
                              @RequestParam(required = false) String comment) {
        return toAjax(limsReportService.approveReport(reportId, approveResult, comment));
    }

    /**
     * 报告放行
     */
    @PreAuthorize("@ss.hasPermi('lims:report:test:release')")
    @Log(title = "报告放行", businessType = BusinessType.UPDATE)
    @PutMapping("/release/{reportId}")
    public AjaxResult release(@PathVariable Long reportId) {
        return toAjax(limsReportService.releaseReport(reportId));
    }

    /**
     * 作废报告
     */
    @PreAuthorize("@ss.hasPermi('lims:report:test:edit')")
    @Log(title = "作废报告", businessType = BusinessType.UPDATE)
    @PutMapping("/void/{reportId}")
    public AjaxResult voidReport(@PathVariable Long reportId, @RequestParam String voidReason) {
        return toAjax(limsReportService.voidReport(reportId, voidReason));
    }

    /**
     * 打印报告（GMP审计追踪：记录打印次数和时间）
     */
    @PreAuthorize("@ss.hasPermi('lims:report:test:query')")
    @Log(title = "打印报告", businessType = BusinessType.OTHER)
    @PutMapping("/print/{reportId}")
    public AjaxResult print(@PathVariable Long reportId) {
        return toAjax(limsReportService.printReport(reportId));
    }

    /**
     * 查询报告签章记录
     */
    @PreAuthorize("@ss.hasPermi('lims:report:test:query')")
    @GetMapping("/signatures/{reportId}")
    public AjaxResult signatures(@PathVariable Long reportId) {
        return AjaxResult.success(limsReportSignatureMapper.selectSignaturesByReportId(reportId));
    }
}
