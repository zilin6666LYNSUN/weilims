package com.wei.lims.instrument.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.instrument.domain.LimsInstrumentReservation;
import com.wei.lims.instrument.service.ILimsInstrumentReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/instrument/reservation")
public class LimsInstrumentReservationController extends BaseController {

    @Autowired
    private ILimsInstrumentReservationService limsInstrumentReservationService;

    @PreAuthorize("@ss.hasPermi('lims:instrument:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsInstrumentReservation reservation) {
        startPage();
        List<LimsInstrumentReservation> list = limsInstrumentReservationService.selectLimsInstrumentReservationList(reservation);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:instrument:query')")
    @GetMapping("/list/{instrumentId}")
    public AjaxResult listByInstrumentId(@PathVariable Long instrumentId) {
        return AjaxResult.success(limsInstrumentReservationService.selectByInstrumentId(instrumentId));
    }

    @PreAuthorize("@ss.hasPermi('lims:instrument:add')")
    @Log(title = "仪器预约", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsInstrumentReservation reservation) {
        return toAjax(limsInstrumentReservationService.insertLimsInstrumentReservation(reservation));
    }

    @PreAuthorize("@ss.hasPermi('lims:instrument:edit')")
    @Log(title = "仪器预约", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsInstrumentReservation reservation) {
        return toAjax(limsInstrumentReservationService.updateLimsInstrumentReservation(reservation));
    }

    @PreAuthorize("@ss.hasPermi('lims:instrument:remove')")
    @Log(title = "仪器预约", businessType = BusinessType.DELETE)
    @DeleteMapping("/{reservationIds}")
    public AjaxResult remove(@PathVariable Long[] reservationIds) {
        return toAjax(limsInstrumentReservationService.deleteLimsInstrumentReservationByIds(reservationIds));
    }
}
