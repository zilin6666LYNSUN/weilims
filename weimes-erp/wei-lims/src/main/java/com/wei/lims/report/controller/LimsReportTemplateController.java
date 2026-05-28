package com.wei.lims.report.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.common.utils.poi.ExcelUtil;
import com.wei.lims.report.domain.LimsReportTemplate;
import com.wei.lims.report.service.ILimsReportTemplateService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 报告模板Controller
 */
@RestController
@RequestMapping("/lims/report/template")
public class LimsReportTemplateController extends BaseController {

    @Autowired
    private ILimsReportTemplateService limsReportTemplateService;

    @PreAuthorize("@ss.hasPermi('lims:report:template:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsReportTemplate template) {
        startPage();
        List<LimsReportTemplate> list = limsReportTemplateService.selectTemplateList(template);
        return getDataTable(list);
    }

    @Log(title = "报告模板", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('lims:report:template:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, LimsReportTemplate template) {
        List<LimsReportTemplate> list = limsReportTemplateService.selectTemplateList(template);
        ExcelUtil<LimsReportTemplate> util = new ExcelUtil<LimsReportTemplate>(LimsReportTemplate.class);
        util.exportExcel(response, list, "报告模板数据");
    }

    @PreAuthorize("@ss.hasPermi('lims:report:template:query')")
    @GetMapping(value = "/{templateId}")
    public AjaxResult getInfo(@PathVariable("templateId") Long templateId) {
        return AjaxResult.success(limsReportTemplateService.selectTemplateByTemplateId(templateId));
    }

    @Log(title = "报告模板", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('lims:report:template:add')")
    @PostMapping
    public AjaxResult add(@RequestBody LimsReportTemplate template) {
        if ("1".equals(limsReportTemplateService.checkTemplateCodeUnique(template.getTemplateCode()))) {
            return AjaxResult.error("新增模板'" + template.getTemplateCode() + "'失败，模板编号已存在");
        }
        template.setCreateBy(getUsername());
        return toAjax(limsReportTemplateService.insertTemplate(template));
    }

    @Log(title = "报告模板", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('lims:report:template:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody LimsReportTemplate template) {
        template.setUpdateBy(getUsername());
        return toAjax(limsReportTemplateService.updateTemplate(template));
    }

    @Log(title = "报告模板", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('lims:report:template:remove')")
    @DeleteMapping("/{templateIds}")
    public AjaxResult remove(@PathVariable Long[] templateIds) {
        return toAjax(limsReportTemplateService.deleteTemplateByTemplateIds(templateIds));
    }
}
