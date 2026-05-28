package com.wei.hrm.common;

import com.wei.hrm.sm.state.StaffState;

/**
 * hrm校验工具类
 *
 * @author wei
 * @date 2024/9/12 21:34
 */
public class HrmCheckUtils {

    /**
     * 校验状态是否可以允许更新
     * @param state 要更新的状态
     * @param currentStatus 当前状态
     */
    public static ThrowExceptionMsg checkStaffStatus(StaffState state, Integer currentStatus) {
        return (errorMsg) -> {
            if (!state.checkCurrentStatusCd(currentStatus)) {
                throw new RuntimeException(errorMsg);
            }
        };
    }
}
