package com.wei.mes.analytics.controller;

import com.wei.common.core.domain.AjaxResult;
import com.wei.mes.wm.service.IWmWarehouseService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据分析
 *
 * @author wei
 * @date 2025/4/13 17:06
 */
@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    @Resource
    private IWmWarehouseService wmWarehouseService;

    @GetMapping("/getStock")
    public AjaxResult getStock() {
        return AjaxResult.success();
    }

    @GetMapping("/getWorkorder")
    public AjaxResult getWorkorder() {
        return AjaxResult.success();
    }

    @GetMapping("/getItem")
    public AjaxResult getItem() {
        return AjaxResult.success();
    }



}
