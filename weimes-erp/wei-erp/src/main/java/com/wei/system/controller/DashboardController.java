package com.wei.system.controller;

import com.wei.common.constant.MsgConstants;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.enums.system.TimeRangeEnum;
import com.wei.system.domain.dto.XYChartDTO;
import com.wei.system.service.IDashboardService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 仪表盘数据统计
 *
 * @author wei
 * @date 2025/4/9 22:10
 */
@RequestMapping("/system/dashboard")
@RestController
public class DashboardController extends BaseController {

    @Resource
    private IDashboardService dashboardService;

    @GetMapping("/loginData/{type}")
    public AjaxResult getLoginData(@PathVariable String type) {
        // 参数校验
        TimeRangeEnum rangeEnum = TimeRangeEnum.getEnumByCode(type);
        if (rangeEnum == null) {
            // 校验失败
            return AjaxResult.error(MsgConstants.PARAM_ERROR);
        }

        // 获取登录数据
        XYChartDTO chartData = dashboardService.getLoginData(rangeEnum);
        return AjaxResult.success(chartData);
    }

}
