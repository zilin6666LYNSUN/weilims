package com.wei.lims.entrust.controller;

import java.util.List;
import com.wei.common.annotation.Log;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.lims.entrust.domain.LimsEntrustItem;
import com.wei.lims.entrust.service.ILimsEntrustItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lims/entrust/item")
public class LimsEntrustItemController extends BaseController {

    @Autowired
    private ILimsEntrustItemService limsentrustitemService;

    @PreAuthorize("@ss.hasPermi('lims:entrust:list')")
    @GetMapping("/list")
    public TableDataInfo list(LimsEntrustItem limsentrustitem) {
        startPage();
        List<LimsEntrustItem> list = limsentrustitemService.selectLimsEntrustItemList(limsentrustitem);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('lims:entrust:query')")
    @GetMapping("/{itemId}")
    public AjaxResult getInfo(@PathVariable Long itemId) {
        return AjaxResult.success(limsentrustitemService.selectLimsEntrustItemByItemId(itemId));
    }

    @PreAuthorize("@ss.hasPermi('lims:entrust:add')")
    @Log(title = "委托检验项目", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LimsEntrustItem limsentrustitem) {
        return toAjax(limsentrustitemService.insertLimsEntrustItem(limsentrustitem));
    }

    @PreAuthorize("@ss.hasPermi('lims:entrust:edit')")
    @Log(title = "委托检验项目", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LimsEntrustItem limsentrustitem) {
        return toAjax(limsentrustitemService.updateLimsEntrustItem(limsentrustitem));
    }

    @PreAuthorize("@ss.hasPermi('lims:entrust:remove')")
    @Log(title = "委托检验项目", businessType = BusinessType.DELETE)
    @DeleteMapping("/{itemIds}")
    public AjaxResult remove(@PathVariable Long[] itemIds) {
        return toAjax(limsentrustitemService.deleteLimsEntrustItemByItemId(itemIds));
    }
}