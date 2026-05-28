package com.wei.lims.reagent.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.reagent.domain.LimsReagent;
import com.wei.lims.reagent.service.ILimsReagentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/reagent")
public class LimsReagentController extends BaseController {

    @Autowired
    private ILimsReagentService limsReagentService;

    @PreAuthorize("@ss.hasPermi('lims:reagent:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsReagent reagent) {
        startPage();
        List<LimsReagent> list = limsReagentService.selectLimsReagentList(reagent);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:reagent:query')")
    @GetMapping("/{reagentId}")
    public AjaxResult getInfo(@PathVariable Long reagentId) {
        return AjaxResult.success(limsReagentService.selectByReagentId(reagentId));
    }

    @PreAuthorize("@ss.hasPermi('lims:reagent:add')")
    @Log(title = "试剂管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsReagent reagent) {
        return toAjax(limsReagentService.insertLimsReagent(reagent));
    }

    @PreAuthorize("@ss.hasPermi('lims:reagent:edit')")
    @Log(title = "试剂管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsReagent reagent) {
        return toAjax(limsReagentService.updateLimsReagent(reagent));
    }

    @PreAuthorize("@ss.hasPermi('lims:reagent:remove')")
    @Log(title = "试剂管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{reagentIds}")
    public AjaxResult remove(@PathVariable Long[] reagentIds) {
        return toAjax(limsReagentService.deleteLimsReagentByIds(reagentIds));
    }
}
