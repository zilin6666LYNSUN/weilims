package com.wei.hrm.sm.service;

import com.wei.hrm.sm.domain.HrmInterviewRecord;
import com.wei.hrm.sm.state.StaffState;

/**
 * 入职申请
 *
 * @author wei
 * @date 2024/9/22 21:19
 */
public interface IHrmEmploymentApplicationService {

    /**
     * 入职申请
     */
    void employmentApplication(HrmInterviewRecord hrmInterviewRecord, StaffState state);

    /**
     * 审核通过
     */
    String pass(HrmInterviewRecord hrmInterviewRecord, StaffState state);
}
