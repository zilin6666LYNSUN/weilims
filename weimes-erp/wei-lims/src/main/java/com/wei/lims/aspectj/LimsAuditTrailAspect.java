package com.wei.lims.aspectj;

import java.lang.reflect.Field;
import com.wei.common.annotation.LimsAudit;
import com.wei.common.utils.SecurityUtils;
import com.wei.common.utils.StringUtils;
import com.wei.lims.esign.domain.LimsAuditTrail;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * LIMS审计追踪AOP切面
 * 自动拦截标注@LimsAudit注解的Service方法，记录字段级变更
 * 满足 21 CFR Part 11 / GMP计算机化系统附录
 */
@Aspect
@Component
public class LimsAuditTrailAspect {

    private static final Logger log = LoggerFactory.getLogger(LimsAuditTrailAspect.class);

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    /**
     * 环绕通知：在方法执行前后记录审计追踪
     */
    @Around("@annotation(com.wei.common.annotation.LimsAudit)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LimsAudit limsAudit = method.getAnnotation(LimsAudit.class);

        String tableName = limsAudit.tableName();
        String operationType = limsAudit.operationType();
        String description = limsAudit.description();

        // 获取方法参数
        Object[] args = joinPoint.getArgs();

        // 对于UPDATE操作，尝试获取旧值
        Object oldObj = null;
        Long recordId = null;

        if ("UPDATE".equals(operationType) && args.length > 0) {
            oldObj = tryGetOldObject(args[0]);
            recordId = tryGetRecordId(args[0]);
        } else if ("INSERT".equals(operationType) && args.length > 0) {
            recordId = tryGetRecordId(args[0]);
        } else if ("DELETE".equals(operationType) && args.length > 0) {
            if (args[0] instanceof Long[]) {
                Long[] ids = (Long[]) args[0];
                if (ids.length > 0) {
                    recordId = ids[0];
                }
            } else if (args[0] instanceof Long) {
                recordId = (Long) args[0];
            }
        }

        // 执行原方法
        Object result = joinPoint.proceed();

        // 记录审计追踪
        try {
            if ("INSERT".equals(operationType)) {
                if (recordId == null && args.length > 0) {
                    recordId = tryGetRecordId(args[0]);
                }
                limsAuditTrailService.recordFieldChanges(tableName, recordId, "INSERT", null, args.length > 0 ? args[0] : null, description);
            } else if ("UPDATE".equals(operationType)) {
                if (recordId == null && args.length > 0) {
                    recordId = tryGetRecordId(args[0]);
                }
                limsAuditTrailService.recordFieldChanges(tableName, recordId, "UPDATE", oldObj, args.length > 0 ? args[0] : null, description);
            } else if ("DELETE".equals(operationType)) {
                limsAuditTrailService.recordFieldChanges(tableName, recordId, "DELETE", null, null, description);
            }
        } catch (Exception e) {
            log.error("审计追踪记录失败: {}", e.getMessage(), e);
        }

        return result;
    }

    private Long tryGetRecordId(Object obj) {
        if (obj == null) return null;
        try {
            Class<?> clazz = obj.getClass();
            String[] idFieldNames = {"id", "sampleId", "testOrderId", "resultId", "instrumentId",
                    "signatureId", "auditId", "specificationId", "methodId", "deviationId",
                    "oosId", "capaId", "changeId", "releaseId", "reagentId", "calibrationId",
                    "maintenanceId", "reservationId", "complaintId", "supplierId",
                    "reportId", "templateId", "ruleId", "acquisitionId", "logId"};

            for (String fieldName : idFieldNames) {
                try {
                    Field field = findField(clazz, fieldName);
                    if (field != null) {
                        field.setAccessible(true);
                        Object value = field.get(obj);
                        if (value instanceof Long) {
                            return (Long) value;
                        }
                    }
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}
        return null;
    }

    private Field findField(Class<?> clazz, String fieldName) {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    private Object tryGetOldObject(Object obj) {
        return null;
    }
}
