package com.wei.lims.esign.service;

import java.util.List;
import com.wei.lims.esign.domain.LimsAuditTrail;

/**
 * 审计追踪Service接口
 */
public interface ILimsAuditTrailService {

    /**
     * 记录审计追踪(仅新增，不提供修改/删除)
     */
    public int recordAuditTrail(LimsAuditTrail limsAuditTrail);

    /**
     * 查询审计追踪列表
     */
    public List<LimsAuditTrail> selectLimsAuditTrailList(LimsAuditTrail limsAuditTrail);

    /**
     * 查询指定表和记录的变更历史
     */
    public List<LimsAuditTrail> selectByTableAndRecord(String tableName, Long recordId);

    /**
     * 自动记录字段级变更(核心方法)
     * @param tableName 表名
     * @param recordId 记录ID
     * @param operationType 操作类型
     * @param oldObj 旧对象
     * @param newObj 新对象
     * @param reason 变更原因
     */
    public void recordFieldChanges(String tableName, Long recordId, String operationType,
                                   Object oldObj, Object newObj, String reason);
}
