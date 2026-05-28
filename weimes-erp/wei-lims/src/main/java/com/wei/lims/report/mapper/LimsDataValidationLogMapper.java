package com.wei.lims.report.mapper;

import java.util.List;
import com.wei.lims.report.domain.LimsDataValidationLog;

/**
 * 数据校验日志Mapper接口
 */
public interface LimsDataValidationLogMapper {

    public LimsDataValidationLog selectLogById(Long logId);

    public List<LimsDataValidationLog> selectLogsByTestOrderId(Long testOrderId);

    public List<LimsDataValidationLog> selectLogList(LimsDataValidationLog log);

    public int insertLog(LimsDataValidationLog log);

    public int deleteLogsByTestOrderId(Long testOrderId);
}
