package com.wei.lims.esign.service.impl;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import com.wei.common.utils.SecurityUtils;
import com.wei.common.utils.StringUtils;
import com.wei.common.utils.ip.IpUtils;
import com.wei.lims.esign.domain.LimsAuditTrail;
import com.wei.lims.esign.mapper.LimsAuditTrailMapper;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import com.wei.common.utils.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 审计追踪Service实现
 */
@Service
public class LimsAuditTrailServiceImpl implements ILimsAuditTrailService {

    @Autowired
    private LimsAuditTrailMapper limsAuditTrailMapper;

    @Override
    public int recordAuditTrail(LimsAuditTrail limsAuditTrail) {
        limsAuditTrail.setOperationTime(new Date());
        limsAuditTrail.setApplicationName("LIMS");
        if (limsAuditTrail.getOperatorId() == null) {
            try {
                limsAuditTrail.setOperatorId(SecurityUtils.getUserId());
                limsAuditTrail.setOperatorName(SecurityUtils.getUsername());
            } catch (Exception e) {
                limsAuditTrail.setOperatorName("SYSTEM");
            }
        }
        try {
            HttpServletRequest request = ServletUtils.getRequest();
            if (request != null) {
                limsAuditTrail.setIpAddress(IpUtils.getIpAddr(request));
                limsAuditTrail.setSessionId(request.getSession().getId());
            }
        } catch (Exception ignored) {}
        return limsAuditTrailMapper.insertLimsAuditTrail(limsAuditTrail);
    }

    @Override
    public List<LimsAuditTrail> selectLimsAuditTrailList(LimsAuditTrail limsAuditTrail) {
        return limsAuditTrailMapper.selectLimsAuditTrailList(limsAuditTrail);
    }

    @Override
    public List<LimsAuditTrail> selectByTableAndRecord(String tableName, Long recordId) {
        return limsAuditTrailMapper.selectByTableAndRecord(tableName, recordId);
    }

    @Override
    public void recordFieldChanges(String tableName, Long recordId, String operationType,
                                  Object oldObj, Object newObj, String reason) {
        if ("INSERT".equals(operationType)) {
            LimsAuditTrail trail = new LimsAuditTrail();
            trail.setTableName(tableName);
            trail.setRecordId(recordId);
            trail.setOperationType("INSERT");
            trail.setReason(reason);
            recordAuditTrail(trail);
            return;
        }

        if ("DELETE".equals(operationType)) {
            LimsAuditTrail trail = new LimsAuditTrail();
            trail.setTableName(tableName);
            trail.setRecordId(recordId);
            trail.setOperationType("DELETE");
            trail.setReason(reason);
            recordAuditTrail(trail);
            return;
        }

        // UPDATE: 逐字段比较记录变更
        if (oldObj != null && newObj != null) {
            Field[] fields = oldObj.getClass().getDeclaredFields();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    Object oldVal = field.get(oldObj);
                    Object newVal = field.get(newObj);
                    String oldStr = oldVal == null ? "" : oldVal.toString();
                    String newStr = newVal == null ? "" : newVal.toString();
                    if (!oldStr.equals(newStr) && StringUtils.isNotEmpty(newStr)) {
                        LimsAuditTrail trail = new LimsAuditTrail();
                        trail.setTableName(tableName);
                        trail.setRecordId(recordId);
                        trail.setFieldName(field.getName());
                        trail.setOldValue(oldStr);
                        trail.setNewValue(newStr);
                        trail.setOperationType("UPDATE");
                        trail.setReason(reason);
                        recordAuditTrail(trail);
                    }
                } catch (Exception ignored) {}
            }
        } else if (newObj != null) {
            // oldObj为空时，至少记录操作级别审计
            LimsAuditTrail trail = new LimsAuditTrail();
            trail.setTableName(tableName);
            trail.setRecordId(recordId);
            trail.setOperationType("UPDATE");
            trail.setReason(reason);
            recordAuditTrail(trail);
        }
    }
}
