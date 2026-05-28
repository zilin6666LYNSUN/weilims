package com.wei.lims.report.service;

import java.util.List;
import com.wei.lims.report.domain.LimsDataValidationLog;
import com.wei.lims.test.domain.LimsTestResult;

/**
 * 数据校验Service接口
 * GMP合规：自动校验检验结果数据
 */
public interface ILimsDataValidationService {

    /**
     * 执行数据校验（对某个检验订单的所有结果执行校验）
     */
    public List<LimsDataValidationLog> validateTestOrder(Long testOrderId);

    /**
     * 执行单条结果校验
     */
    public List<LimsDataValidationLog> validateResult(Long testOrderId, LimsTestResult result);

    /**
     * 查询校验日志
     */
    public List<LimsDataValidationLog> selectValidationLogs(Long testOrderId);

    /**
     * 检查检验订单是否通过所有校验
     */
    public boolean isAllValidationPassed(Long testOrderId);
}
