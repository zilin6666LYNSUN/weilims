package com.wei.lims.reagent.controller;

import java.util.List;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.lims.reagent.domain.LimsReagentStockLog;
import com.wei.lims.reagent.service.ILimsReagentStockLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/reagent/stocklog")
public class LimsReagentStockLogController extends BaseController {

    @Autowired
    private ILimsReagentStockLogService limsReagentStockLogService;

    @PreAuthorize("@ss.hasPermi('lims:reagent:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsReagentStockLog stockLog) {
        startPage();
        List<LimsReagentStockLog> list = limsReagentStockLogService.selectLimsReagentStockLogList(stockLog);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:reagent:query')")
    @GetMapping("/list/{reagentId}")
    public AjaxResult listByReagentId(@PathVariable Long reagentId) {
        return AjaxResult.success(limsReagentStockLogService.selectByReagentId(reagentId));
    }

    @PreAuthorize("@ss.hasPermi('lims:reagent:add')")
    @PostMapping
    public AjaxResult add(@RequestBody LimsReagentStockLog stockLog) {
        return toAjax(limsReagentStockLogService.insertLimsReagentStockLog(stockLog));
    }
}
