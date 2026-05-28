package com.wei.system.service;

import com.wei.common.enums.system.TimeRangeEnum;
import com.wei.system.domain.dto.XYChartDTO;

/**
 * 仪表盘数据统计
 *
 * @author wei
 * @date 2025/4/9 22:12
 */
public interface IDashboardService {

    /**
     * 获取登录数据
     */
    XYChartDTO getLoginData(TimeRangeEnum timeRangeEnum);
}
