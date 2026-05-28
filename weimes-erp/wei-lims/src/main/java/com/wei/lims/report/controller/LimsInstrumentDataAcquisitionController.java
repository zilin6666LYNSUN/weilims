package com.wei.lims.report.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.report.domain.LimsInstrumentDataAcquisition;
import com.wei.lims.report.service.ILimsInstrumentDataAcquisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 仪器数据采集配置Controller
 */
@RestController
@RequestMapping("/lims/instrument/acquisition")
public class LimsInstrumentDataAcquisitionController extends BaseController {

    @Autowired
    private ILimsInstrumentDataAcquisitionService acquisitionService;

    @PreAuthorize("@ss.hasPermi('lims:instrument:acquisition:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsInstrumentDataAcquisition acquisition) {
        startPage();
        List<LimsInstrumentDataAcquisition> list = acquisitionService.selectAcquisitionList(acquisition);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:instrument:acquisition:query')")
    @GetMapping(value = "/{acquisitionId}")
    public AjaxResult getInfo(@PathVariable("acquisitionId") Long acquisitionId) {
        return AjaxResult.success(acquisitionService.selectAcquisitionById(acquisitionId));
    }

    @PreAuthorize("@ss.hasPermi('lims:instrument:acquisition:query')")
    @GetMapping(value = "/instrument/{instrumentId}")
    public AjaxResult getByInstrumentId(@PathVariable("instrumentId") Long instrumentId) {
        return AjaxResult.success(acquisitionService.selectAcquisitionByInstrumentId(instrumentId));
    }

    @PreAuthorize("@ss.hasPermi('lims:instrument:acquisition:add')")
    @Log(title = "仪器采集配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsInstrumentDataAcquisition acquisition) {
        return toAjax(acquisitionService.insertAcquisition(acquisition));
    }

    @PreAuthorize("@ss.hasPermi('lims:instrument:acquisition:edit')")
    @Log(title = "仪器采集配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsInstrumentDataAcquisition acquisition) {
        return toAjax(acquisitionService.updateAcquisition(acquisition));
    }

    @PreAuthorize("@ss.hasPermi('lims:instrument:acquisition:remove')")
    @Log(title = "仪器采集配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{acquisitionIds}")
    public AjaxResult remove(@PathVariable Long[] acquisitionIds) {
        return toAjax(acquisitionService.deleteAcquisitionByIds(acquisitionIds));
    }

    /**
     * 模拟从仪器采集数据
     */
    @PreAuthorize("@ss.hasPermi('lims:instrument:acquisition:edit')")
    @Log(title = "仪器数据采集", businessType = BusinessType.OTHER)
    @PostMapping("/import")
    public AjaxResult importData(@RequestParam Long instrumentId, @RequestParam Long testOrderId) {
        int count = acquisitionService.importInstrumentData(instrumentId, testOrderId);
        return AjaxResult.success("成功采集" + count + "条数据");
    }
}
