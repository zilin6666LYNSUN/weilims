package com.wei.hrm.sm.vo;

import com.wei.common.annotation.Excel;
import com.wei.hrm.sm.domain.HrmStaff;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 员工花名册对象 hrm_staff
 *
 * @author 施子安
 * @date 2024-07-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class HrmStaffVo extends HrmStaff {
    private static final long serialVersionUID = 1L;

    /**
     * 职级层次名称
     */
    @Excel(name = "职级层次")
    private String rankType;

    /**
     * 薪资范围下限
     */
    @Excel(name = "薪资范围下限")
    private BigDecimal salaryRangeMin;


    /**
     * 薪资范围上限
     */
    @Excel(name = "薪资范围上限")
    private BigDecimal salaryRangeMax;

}
