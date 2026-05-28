package com.wei.lims.instrument.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.instrument.domain.LimsInstrumentMaintenance;
import com.wei.lims.instrument.service.ILimsInstrumentMaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/instrument/maintenance")
public class LimsInstrumentMaintenanceController extends BaseController {

    @Autowired
    private ILimsInstrumentMaintenanceService limsInstrumentMaintenanceService;

    @PreAuthorize("@ss.hasPermi('lims:instrument:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsInstrumentMaintenance maintenance) {
        startPage();
        List<LimsInstrumentMaintenance> list = limsInstrumentMaintenanceService.selectLimsInstrumentMaintenanceList(maintenance);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:instrument:query')")
    @GetMapping("/list/{instrumentId}")
    public AjaxResult listByInstrumentId(@PathVariable Long instrumentId) {
        return AjaxResult.success(limsInstrumentMaintenanceService.selectByInstrumentId(instrumentId));
    }

    @PreAuthorize("@ss.hasPermi('lims:instrument:add')")
    @Log(title = "仪器维护", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsInstrumentMaintenance maintenance) {
        return toAjax(limsInstrumentMaintenanceService.insertLimsInstrumentMaintenance(maintenance));
    }

    @PreAuthorize("@ss.hasPermi('lims:instrument:edit')")
    @Log(title = "仪器维护", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsInstrumentMaintenance maintenance) {
        return toAjax(limsInstrumentMaintenanceService.updateLimsInstrumentMaintenance(maintenance));
    }

    @PreAuthorize("@ss.hasPermi('lims:instrument:remove')")
    @Log(title = "仪器维护", businessType = BusinessType.DELETE)
    @DeleteMapping("/{maintenanceIds}")
    public AjaxResult remove(@PathVariable Long[] maintenanceIds) {
        return toAjax(limsInstrumentMaintenanceService.deleteLimsInstrumentMaintenanceByIds(maintenanceIds));
    }
}
