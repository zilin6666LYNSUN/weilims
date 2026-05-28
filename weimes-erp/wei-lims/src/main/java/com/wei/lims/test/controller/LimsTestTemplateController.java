package com.wei.lims.test.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.test.domain.LimsTestTemplate;
import com.wei.lims.test.domain.LimsTestTemplateItem;
import com.wei.lims.test.service.ILimsTestTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 检验模板Controller
 */
@RestController
@RequestMapping("/lims/test/template")
public class LimsTestTemplateController extends BaseController {

    @Autowired
    private ILimsTestTemplateService limsTestTemplateService;

    @PreAuthorize("@ss.hasPermi('lims:test:template:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsTestTemplate template) {
        startPage();
        List<LimsTestTemplate> list = limsTestTemplateService.selectLimsTestTemplateList(template);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:test:template:query')")
    @GetMapping("/{templateId}")
    public AjaxResult getInfo(@PathVariable Long templateId) {
        return AjaxResult.success(limsTestTemplateService.selectLimsTestTemplateByTemplateId(templateId));
    }

    @PreAuthorize("@ss.hasPermi('lims:test:template:add')")
    @Log(title = "检验模板", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsTestTemplate template) {
        return toAjax(limsTestTemplateService.insertLimsTestTemplate(template));
    }

    @PreAuthorize("@ss.hasPermi('lims:test:template:edit')")
    @Log(title = "检验模板", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsTestTemplate template) {
        return toAjax(limsTestTemplateService.updateLimsTestTemplate(template));
    }

    @PreAuthorize("@ss.hasPermi('lims:test:template:remove')")
    @Log(title = "检验模板", businessType = BusinessType.DELETE)
    @DeleteMapping("/{templateIds}")
    public AjaxResult remove(@PathVariable Long[] templateIds) {
        return toAjax(limsTestTemplateService.deleteLimsTestTemplateByTemplateIds(templateIds));
    }

    /** 模板检测项 */
    @PreAuthorize("@ss.hasPermi('lims:test:template:query')")
    @GetMapping("/item/list/{templateId}")
    public AjaxResult itemList(@PathVariable Long templateId) {
        return AjaxResult.success(limsTestTemplateService.selectItemsByTemplateId(templateId));
    }

    @PreAuthorize("@ss.hasPermi('lims:test:template:add')")
    @PostMapping("/item")
    public AjaxResult addItem(@RequestBody LimsTestTemplateItem item) {
        return toAjax(limsTestTemplateService.insertTemplateItem(item));
    }

    @PreAuthorize("@ss.hasPermi('lims:test:template:edit')")
    @PutMapping("/item")
    public AjaxResult editItem(@RequestBody LimsTestTemplateItem item) {
        return toAjax(limsTestTemplateService.updateTemplateItem(item));
    }

    @PreAuthorize("@ss.hasPermi('lims:test:template:remove')")
    @DeleteMapping("/item/{ids}")
    public AjaxResult removeItem(@PathVariable Long[] ids) {
        return toAjax(limsTestTemplateService.deleteTemplateItemByIds(ids));
    }
}
