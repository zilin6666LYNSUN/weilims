package com.wei.lims.esign.mapper;

import java.util.List;
import com.wei.lims.esign.domain.LimsAuditTrail;

/**
 * 审计追踪Mapper接口
 */
public interface LimsAuditTrailMapper {

    /**
     * 新增审计追踪记录(仅INSERT)
     */
    public int insertLimsAuditTrail(LimsAuditTrail limsAuditTrail);

    /**
     * 查询审计追踪(只读查询，不提供更新/删除)
     */
    public List<LimsAuditTrail> selectLimsAuditTrailList(LimsAuditTrail limsAuditTrail);

    /**
     * 查询单条记录
     */
    public LimsAuditTrail selectLimsAuditTrailById(Long auditId);

    /**
     * 根据表名和记录ID查询变更历史
     */
    public List<LimsAuditTrail> selectByTableAndRecord(String tableName, Long recordId);
}
