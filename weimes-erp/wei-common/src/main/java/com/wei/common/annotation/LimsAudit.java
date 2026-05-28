package com.wei.common.annotation;

import java.lang.annotation.*;

/**
 * LIMS审计追踪注解
 * 标注在需要自动记录审计追踪的Service方法上
 * 满足 21 CFR Part 11 / GMP计算机化系统附录要求
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LimsAudit {

    /** 操作的表名 */
    String tableName();

    /** 操作类型: INSERT/UPDATE/DELETE */
    String operationType();

    /** 操作描述(变更原因) */
    String description() default "";
}
