package com.wei.mes.wm.controller;

import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.common.utils.DateUtils;
import com.wei.common.utils.poi.ExcelUtil;
import com.wei.mes.wm.domain.WmMaterialStock;
import com.wei.mes.wm.service.IWmMaterialStockService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 库存记录Controller
 *
 * @author yinjinlu
 * @date 2022-05-30
 */
@RestController
@RequestMapping("/wm/wm-material-stock-log")
public class WmMaterialStockController extends BaseController {
    @Autowired
    private IWmMaterialStockService wmMaterialStockService;

    /**
     * 查询库存记录列表
     */
    @GetMapping("/list")
    public TableDataInfo list(WmMaterialStock wmMaterialStock) {
        startPage();
        List<WmMaterialStock> list = wmMaterialStockService.selectWmMaterialStockList(wmMaterialStock);
        return getDataTable(list);
    }

    /**
     * 导出库存记录列表
     */
    @PreAuthorize("@ss.hasPermi('mes:wm:wmstock:export')")
    @Log(title = "库存记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WmMaterialStock wmMaterialStock) {
        List<WmMaterialStock> list = wmMaterialStockService.selectWmMaterialStockList(wmMaterialStock);
        ExcelUtil<WmMaterialStock> util = new ExcelUtil<WmMaterialStock>(WmMaterialStock.class);
        util.exportExcel(response, list, "库存记录数据");
    }

    /**
     * 获取库存记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('mes:wm:wmstock:query')")
    @GetMapping(value = "/{materialStockId}")
    public AjaxResult getInfo(@PathVariable("materialStockId") Long materialStockId) {
        return AjaxResult.success(wmMaterialStockService.selectWmMaterialStockByMaterialStockId(materialStockId));
    }

    /**
     * 新增库存记录
     */
    @PreAuthorize("@ss.hasPermi('mes:wm:wmstock:add')")
    @Log(title = "库存记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WmMaterialStock wmMaterialStock) {
        return toAjax(wmMaterialStockService.insertWmMaterialStock(wmMaterialStock));
    }

    /**
     * 修改库存记录
     */
    @PreAuthorize("@ss.hasPermi('mes:wm:wmstock:edit')")
    @Log(title = "库存记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WmMaterialStock wmMaterialStock) {
        return toAjax(wmMaterialStockService.updateWmMaterialStock(wmMaterialStock));
    }

    /**
     * 删除库存记录
     */
    @PreAuthorize("@ss.hasPermi('mes:wm:wmstock:remove')")
    @Log(title = "库存记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{materialStockIds}")
    public AjaxResult remove(@PathVariable Long[] materialStockIds) {
        return toAjax(wmMaterialStockService.deleteWmMaterialStockByMaterialStockIds(materialStockIds));
    }


    /**
     * 大屏-统计库存数量 根据物料id和年月
     * +
     */
    @PreAuthorize("@ss.hasPermi('mes:wm:wmstock:materielCount')")
    @PostMapping("/materielCount")
    public AjaxResult materielCount(@RequestBody Map map) {
        String itemCode = (String) map.get("itemCode");
        String dateTime = (String) map.get("recptDate");
        return AjaxResult.success(wmMaterialStockService.selectMaterielCount(itemCode, DateUtils.parseDate(dateTime)));
    }

}
