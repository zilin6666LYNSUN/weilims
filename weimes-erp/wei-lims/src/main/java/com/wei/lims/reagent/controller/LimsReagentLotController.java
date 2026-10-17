package com.wei.lims.reagent.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.reagent.domain.LimsReagentLot;
import com.wei.lims.reagent.service.ILimsReagentLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/reagent/lot")
public class LimsReagentLotController extends BaseController {

    @Autowired
    private ILimsReagentLotService limsReagentLotService;

    @PreAuthorize("@ss.hasPermi('lims:reagent:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsReagentLot lot) {
        startPage();
        List<LimsReagentLot> list = limsReagentLotService.selectLimsReagentLotList(lot);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:reagent:query')")
    @GetMapping("/list/{reagentId}")
    public AjaxResult listByReagentId(@PathVariable Long reagentId) {
        return AjaxResult.success(limsReagentLotService.selectByReagentId(reagentId));
    }

    @PreAuthorize("@ss.hasPermi('lims:reagent:query')")
    @GetMapping("/{lotId}")
    public AjaxResult getInfo(@PathVariable Long lotId) {
        return AjaxResult.success(limsReagentLotService.selectByLotId(lotId));
    }

    @PreAuthorize("@ss.hasPermi('lims:reagent:add')")
    @Log(title = "试剂批次效期", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsReagentLot lot) {
        return toAjax(limsReagentLotService.insertLimsReagentLot(lot));
    }

    @PreAuthorize("@ss.hasPermi('lims:reagent:edit')")
    @Log(title = "试剂批次效期", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsReagentLot lot) {
        return toAjax(limsReagentLotService.updateLimsReagentLot(lot));
    }

    @PreAuthorize("@ss.hasPermi('lims:reagent:remove')")
    @Log(title = "试剂批次效期", businessType = BusinessType.DELETE)
    @DeleteMapping("/{lotIds}")
    public AjaxResult remove(@PathVariable Long[] lotIds) {
        return toAjax(limsReagentLotService.deleteLimsReagentLotByIds(lotIds));
    }
}
