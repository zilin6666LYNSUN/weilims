package com.wei.lims.instrument.controller;

import java.util.List;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.lims.instrument.domain.LimsInstrumentType;
import com.wei.lims.instrument.service.ILimsInstrumentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/instrument/type")
public class LimsInstrumentTypeController extends BaseController {

    @Autowired
    private ILimsInstrumentTypeService limsInstrumentTypeService;

    @PreAuthorize("@ss.hasPermi('lims:instrument:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsInstrumentType type) {
        startPage();
        List<LimsInstrumentType> list = limsInstrumentTypeService.selectLimsInstrumentTypeList(type);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:instrument:query')")
    @GetMapping("/{typeId}")
    public AjaxResult getInfo(@PathVariable Long typeId) {
        return AjaxResult.success(limsInstrumentTypeService.selectByTypeId(typeId));
    }

    @PreAuthorize("@ss.hasPermi('lims:instrument:add')")
    @PostMapping
    public AjaxResult add(@RequestBody LimsInstrumentType type) {
        return toAjax(limsInstrumentTypeService.insertLimsInstrumentType(type));
    }

    @PreAuthorize("@ss.hasPermi('lims:instrument:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody LimsInstrumentType type) {
        return toAjax(limsInstrumentTypeService.updateLimsInstrumentType(type));
    }

    @PreAuthorize("@ss.hasPermi('lims:instrument:remove')")
    @DeleteMapping("/{typeIds}")
    public AjaxResult remove(@PathVariable Long[] typeIds) {
        return toAjax(limsInstrumentTypeService.deleteLimsInstrumentTypeByIds(typeIds));
    }
}
