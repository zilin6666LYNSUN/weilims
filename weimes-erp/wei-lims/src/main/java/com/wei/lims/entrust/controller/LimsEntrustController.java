package com.wei.lims.entrust.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.entrust.domain.LimsEntrust;
import com.wei.lims.entrust.service.ILimsEntrustService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/entrust")
public class LimsEntrustController extends BaseController {

    @Autowired
    private ILimsEntrustService limsentrustService;

    @PreAuthorize("@ss.hasPermi('lims:entrust:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsEntrust limsentrust) {
        startPage();
        List<LimsEntrust> list = limsentrustService.selectLimsEntrustList(limsentrust);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:entrust:query')")
    @GetMapping("/{entrustId}")
    public AjaxResult getInfo(@PathVariable Long entrustId) {
        return AjaxResult.success(limsentrustService.selectLimsEntrustByEntrustId(entrustId));
    }

    @PreAuthorize("@ss.hasPermi('lims:entrust:add')")
    @Log(title = "委托单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsEntrust limsentrust) {
        return toAjax(limsentrustService.insertLimsEntrust(limsentrust));
    }

    @PreAuthorize("@ss.hasPermi('lims:entrust:edit')")
    @Log(title = "委托单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsEntrust limsentrust) {
        return toAjax(limsentrustService.updateLimsEntrust(limsentrust));
    }

    @PreAuthorize("@ss.hasPermi('lims:entrust:remove')")
    @Log(title = "委托单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{entrustIds}")
    public AjaxResult remove(@PathVariable Long[] entrustIds) {
        return toAjax(limsentrustService.deleteLimsEntrustByEntrustId(entrustIds));
    }

    @PreAuthorize("@ss.hasPermi('lims:entrust:submit')")
    @Log(title = "委托单", businessType = BusinessType.UPDATE)
    @PutMapping("/submit/{entrustId}")
    public AjaxResult submit(@PathVariable Long entrustId) {
        return toAjax(limsentrustService.submitLimsEntrust(entrustId));
    }

    @PreAuthorize("@ss.hasPermi('lims:entrust:review')")
    @Log(title = "委托单", businessType = BusinessType.UPDATE)
    @PutMapping("/review")
    public AjaxResult review(@RequestBody LimsEntrust entrust) {
        return toAjax(limsentrustService.reviewLimsEntrust(entrust));
    }

    @PreAuthorize("@ss.hasPermi('lims:entrust:register')")
    @Log(title = "委托单", businessType = BusinessType.UPDATE)
    @PutMapping("/register/{entrustId}")
    public AjaxResult register(@PathVariable Long entrustId) {
        return AjaxResult.success("data", limsentrustService.registerSample(entrustId));
    }
}