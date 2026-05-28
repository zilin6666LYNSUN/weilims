package com.wei.lims.instrument.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.instrument.domain.LimsInstrumentCalibration;
import com.wei.lims.instrument.service.ILimsInstrumentCalibrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/instrument/calibration")
public class LimsInstrumentCalibrationController extends BaseController {

    @Autowired
    private ILimsInstrumentCalibrationService limsInstrumentCalibrationService;

    @PreAuthorize("@ss.hasPermi('lims:instrument:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsInstrumentCalibration calibration) {
        startPage();
        List<LimsInstrumentCalibration> list = limsInstrumentCalibrationService.selectLimsInstrumentCalibrationList(calibration);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:instrument:query')")
    @GetMapping("/list/{instrumentId}")
    public AjaxResult listByInstrumentId(@PathVariable Long instrumentId) {
        return AjaxResult.success(limsInstrumentCalibrationService.selectByInstrumentId(instrumentId));
    }

    @PreAuthorize("@ss.hasPermi('lims:instrument:add')")
    @Log(title = "仪器校准", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsInstrumentCalibration calibration) {
        return toAjax(limsInstrumentCalibrationService.insertLimsInstrumentCalibration(calibration));
    }

    @PreAuthorize("@ss.hasPermi('lims:instrument:edit')")
    @Log(title = "仪器校准", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsInstrumentCalibration calibration) {
        return toAjax(limsInstrumentCalibrationService.updateLimsInstrumentCalibration(calibration));
    }

    @PreAuthorize("@ss.hasPermi('lims:instrument:remove')")
    @Log(title = "仪器校准", businessType = BusinessType.DELETE)
    @DeleteMapping("/{calibrationIds}")
    public AjaxResult remove(@PathVariable Long[] calibrationIds) {
        return toAjax(limsInstrumentCalibrationService.deleteLimsInstrumentCalibrationByIds(calibrationIds));
    }
}
