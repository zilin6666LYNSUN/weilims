package com.wei.lims.reagent.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.reagent.domain.LimsCultureMedia;
import com.wei.lims.reagent.service.ILimsCultureMediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/reagent/media")
public class LimsCultureMediaController extends BaseController {

    @Autowired
    private ILimsCultureMediaService limsCultureMediaService;

    @PreAuthorize("@ss.hasPermi('lims:reagent:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsCultureMedia media) {
        startPage();
        List<LimsCultureMedia> list = limsCultureMediaService.selectLimsCultureMediaList(media);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:reagent:query')")
    @GetMapping("/{mediaId}")
    public AjaxResult getInfo(@PathVariable Long mediaId) {
        return AjaxResult.success(limsCultureMediaService.selectByMediaId(mediaId));
    }

    @PreAuthorize("@ss.hasPermi('lims:reagent:add')")
    @Log(title = "培养基管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsCultureMedia media) {
        return toAjax(limsCultureMediaService.insertLimsCultureMedia(media));
    }

    @PreAuthorize("@ss.hasPermi('lims:reagent:edit')")
    @Log(title = "培养基管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsCultureMedia media) {
        return toAjax(limsCultureMediaService.updateLimsCultureMedia(media));
    }

    @PreAuthorize("@ss.hasPermi('lims:reagent:remove')")
    @Log(title = "培养基管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{mediaIds}")
    public AjaxResult remove(@PathVariable Long[] mediaIds) {
        return toAjax(limsCultureMediaService.deleteLimsCultureMediaByIds(mediaIds));
    }
}
