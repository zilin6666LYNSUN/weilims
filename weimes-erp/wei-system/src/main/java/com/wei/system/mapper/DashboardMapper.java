package com.wei.system.mapper;

import com.wei.common.enums.system.TimeRangeEnum;
import com.wei.system.domain.dto.XYChartDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 仪表盘数据统计
 *
 * @author wei
 * @date 2025/4/9 22:14
 */
@Mapper
public interface DashboardMapper {

    List<XYChartDTO.XYSeries> getLoginDataByTimeRange(@Param("timeRange") TimeRangeEnum timeRange);

}
