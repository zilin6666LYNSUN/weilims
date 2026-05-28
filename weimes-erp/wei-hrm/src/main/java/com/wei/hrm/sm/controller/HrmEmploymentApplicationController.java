package com.wei.hrm.sm.controller;

import com.github.pagehelper.Page;
import com.wei.common.annotation.Log;
import com.wei.common.constant.MsgConstants;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.common.enums.hrm.StaffStatusEnum;
import com.wei.common.exception.BusinessException;
import com.wei.hrm.common.HrmCheckUtils;
import com.wei.hrm.sm.domain.HrmInterviewRecord;
import com.wei.hrm.sm.domain.HrmStaff;
import com.wei.hrm.sm.dto.InterviewRecordDTO;
import com.wei.hrm.sm.service.IHrmEmploymentApplicationService;
import com.wei.hrm.sm.service.IHrmInterviewRecordService;
import com.wei.hrm.sm.service.IHrmStaffService;
import com.wei.hrm.sm.state.StaffState;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 入职申请
 *
 * @author wei
 * @date 2024/9/11 13:34
 */
@RestController
@RequestMapping("/sm/hrm-employment-application")
public class HrmEmploymentApplicationController extends BaseController {

    @Resource
    private IHrmInterviewRecordService interviewRecordService;
    @Resource
    private IHrmStaffService hrmStaffService;
    @Resource
    private IHrmEmploymentApplicationService employmentApplicationService;

    /**
     * 查询入职申请列表
     */
    @PreAuthorize("@ss.hasPermi('sm:employmentapplication:list')")
    @GetMapping("/list")
    public TableDataInfo listTalents(HrmInterviewRecord query) {
        startPage();
        Page<InterviewRecordDTO> result =
                this.interviewRecordService.pageGroupByStaffWithStatus(query,
                        List.of(
                                StaffStatusEnum.PASS_THE_INTERVIEW.getCode(),
                                StaffStatusEnum.ALLOW_ENTRY.getCode(),
                                StaffStatusEnum.REFUSE_ENTRY.getCode(),
                                StaffStatusEnum.EMPLOYMENT_APPLICATION.getCode(),
                                StaffStatusEnum.PROBATION.getCode(),
                                StaffStatusEnum.BECOME_A_REGULAR_WORKER.getCode()
                        ));
        return getDataTable(result);
    }

    /**
     * 查询待审核列表
     */
    @PreAuthorize("@ss.hasPermi('sm:employmentapplication:list')")
    @GetMapping("/listAudit")
    public TableDataInfo listAudit(HrmInterviewRecord query) {
        startPage();
        Page<InterviewRecordDTO> result =
                this.interviewRecordService.pageGroupByStaffWithStatus(query,
                        List.of(
                                StaffStatusEnum.ALLOW_ENTRY.getCode(),
                                StaffStatusEnum.REFUSE_ENTRY.getCode(),
                                StaffStatusEnum.EMPLOYMENT_APPLICATION.getCode()
                        ));
        return getDataTable(result);
    }

    /**
     * 入职申请
     */
    @PreAuthorize("@ss.hasPermi('sm:employmentapplication:add')")
    @Log(title = "入职申请", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HrmInterviewRecord hrmInterviewRecord) {
        // 校验员工数据
        HrmStaff staff = this.hrmStaffService.getById(hrmInterviewRecord.getStaffId());
        Optional.ofNullable(staff).orElseThrow(() -> new BusinessException(MsgConstants.PARAM_ERROR));
        // 校验当前状态是否允许执行此操作
        StaffState state = StaffState.EMPLOYMENT_APPLICATION;
        HrmCheckUtils.checkStaffStatus(state, staff.getStatus()).throwMsg(MsgConstants.ERROR_STATUS);
        this.employmentApplicationService.employmentApplication(hrmInterviewRecord, state);
        return AjaxResult.success();
    }

    /**
     * 审核通过
     */
    @PreAuthorize("@ss.hasPermi('sm:employmentapplication:add')")
    @Log(title = "审核通过", businessType = BusinessType.UPDATE)
    @PutMapping("/pass")
    public AjaxResult pass(@RequestBody HrmInterviewRecord hrmInterviewRecord) {
        Optional.ofNullable(hrmInterviewRecord.getStaffId()).orElseThrow(() -> new BusinessException(MsgConstants.PARAM_ERROR));
        // 校验员工数据
        HrmStaff staff = this.hrmStaffService.getById(hrmInterviewRecord.getStaffId());
        Optional.ofNullable(staff).orElseThrow(() -> new BusinessException(MsgConstants.PARAM_ERROR));
        // 校验当前状态是否允许执行此操作
        StaffState state = StaffState.ALLOW_ENTRY;
        HrmCheckUtils.checkStaffStatus(state, staff.getStatus()).throwMsg(MsgConstants.ERROR_STATUS);
        String msg = this.employmentApplicationService.pass(hrmInterviewRecord,state);
        return AjaxResult.success(msg);
    }

    /**
     * 审核拒绝
     */
    @PreAuthorize("@ss.hasPermi('sm:employmentapplication:add')")
    @Log(title = "审核拒绝", businessType = BusinessType.UPDATE)
    @PutMapping("/refuse")
    public AjaxResult refuse(@RequestBody HrmInterviewRecord hrmInterviewRecord) {
        // 校验员工数据
        Optional.ofNullable(hrmInterviewRecord.getStaffId()).orElseThrow(() -> new BusinessException(MsgConstants.PARAM_ERROR));
        HrmStaff staff = this.hrmStaffService.getById(hrmInterviewRecord.getStaffId());
        Optional.ofNullable(staff).orElseThrow(() -> new BusinessException(MsgConstants.PARAM_ERROR));
        // 校验当前状态是否允许执行此操作
        StaffState state = StaffState.REFUSE_ENTRY;
        HrmCheckUtils.checkStaffStatus(state, staff.getStatus()).throwMsg(MsgConstants.ERROR_STATUS);

        return AjaxResult.success();
    }
}
