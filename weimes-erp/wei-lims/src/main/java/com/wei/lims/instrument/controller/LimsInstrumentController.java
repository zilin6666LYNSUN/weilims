package com.wei.lims.instrument.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.constant.UserConstants;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.common.utils.poi.ExcelUtil;
import com.wei.lims.instrument.domain.LimsInstrument;
import com.wei.lims.instrument.service.ILimsInstrumentService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 仪器管理Controller
 */
@RestController
@RequestMapping("/lims/instrument")
public class LimsInstrumentController extends BaseController {

    @Autowired
    private ILimsInstrumentService limsInstrumentService;

    @PreAuthorize("@ss.hasPermi('lims:instrument:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsInstrument limsInstrument) {
        startPage();
        List<LimsInstrument> list = limsInstrumentService.selectLimsInstrumentList(limsInstrument);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:instrument:export')")
    @Log(title = "仪器台账", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LimsInstrument limsInstrument) {
        List<LimsInstrument> list = limsInstrumentService.selectLimsInstrumentList(limsInstrument);
        ExcelUtil<LimsInstrument> util = new ExcelUtil<LimsInstrument>(LimsInstrument.class);
        util.exportExcel(response, list, "仪器台账数据");
    }

    @PreAuthorize("@ss.hasPermi('lims:instrument:query')")
    @GetMapping(value = "/{instrumentId}")
    public AjaxResult getInfo(@PathVariable("instrumentId") Long instrumentId) {
        return AjaxResult.success(limsInstrumentService.selectLimsInstrumentByInstrumentId(instrumentId));
    }

    @PreAuthorize("@ss.hasPermi('lims:instrument:add')")
    @Log(title = "仪器台账", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsInstrument limsInstrument) {
        if (UserConstants.NOT_UNIQUE.equals(limsInstrumentService.checkInstrumentCodeUnique(limsInstrument))) {
            return AjaxResult.error("仪器编号已存在！");
        }
        return toAjax(limsInstrumentService.insertLimsInstrument(limsInstrument));
    }

    @PreAuthorize("@ss.hasPermi('lims:instrument:edit')")
    @Log(title = "仪器台账", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsInstrument limsInstrument) {
        if (UserConstants.NOT_UNIQUE.equals(limsInstrumentService.checkInstrumentCodeUnique(limsInstrument))) {
            return AjaxResult.error("仪器编号已存在！");
        }
        return toAjax(limsInstrumentService.updateLimsInstrument(limsInstrument));
    }

    @PreAuthorize("@ss.hasPermi('lims:instrument:remove')")
    @Log(title = "仪器台账", businessType = BusinessType.DELETE)
    @DeleteMapping("/{instrumentIds}")
    public AjaxResult remove(@PathVariable Long[] instrumentIds) {
        return toAjax(limsInstrumentService.deleteLimsInstrumentByInstrumentIds(instrumentIds));
    }

    /**
     * 校准到期提醒
     */
    @PreAuthorize("@ss.hasPermi('lims:instrument:list')")
    @GetMapping("/calibration-due/{withinDays}")
    public AjaxResult calibrationDue(@PathVariable int withinDays) {
        return AjaxResult.success(limsInstrumentService.selectCalibrationDueList(withinDays));
    }
}
