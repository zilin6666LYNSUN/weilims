package com.wei.lims.entrust.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.entrust.domain.LimsEntrustTemplate;
import com.wei.lims.entrust.service.ILimsEntrustTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/entrust/template")
public class LimsEntrustTemplateController extends BaseController {

    @Autowired
    private ILimsEntrustTemplateService limsentrusttemplateService;

    @PreAuthorize("@ss.hasPermi('lims:entrust:template:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsEntrustTemplate limsentrusttemplate) {
        startPage();
        List<LimsEntrustTemplate> list = limsentrusttemplateService.selectLimsEntrustTemplateList(limsentrusttemplate);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:entrust:template:query')")
    @GetMapping("/{templateId}")
    public AjaxResult getInfo(@PathVariable Long templateId) {
        return AjaxResult.success(limsentrusttemplateService.selectLimsEntrustTemplateByTemplateId(templateId));
    }

    @PreAuthorize("@ss.hasPermi('lims:entrust:template:add')")
    @Log(title = "委托单模板", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsEntrustTemplate limsentrusttemplate) {
        return toAjax(limsentrusttemplateService.insertLimsEntrustTemplate(limsentrusttemplate));
    }

    @PreAuthorize("@ss.hasPermi('lims:entrust:template:edit')")
    @Log(title = "委托单模板", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsEntrustTemplate limsentrusttemplate) {
        return toAjax(limsentrusttemplateService.updateLimsEntrustTemplate(limsentrusttemplate));
    }

    @PreAuthorize("@ss.hasPermi('lims:entrust:template:remove')")
    @Log(title = "委托单模板", businessType = BusinessType.DELETE)
    @DeleteMapping("/{templateIds}")
    public AjaxResult remove(@PathVariable Long[] templateIds) {
        return toAjax(limsentrusttemplateService.deleteLimsEntrustTemplateByTemplateId(templateIds));
    }
}