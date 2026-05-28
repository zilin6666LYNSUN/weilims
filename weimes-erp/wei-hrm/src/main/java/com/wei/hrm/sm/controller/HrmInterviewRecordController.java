package com.wei.hrm.sm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wei.common.annotation.Log;
import com.wei.common.constant.MsgConstants;
import com.wei.common.core.controller.BaseController;
import com.wei.common.core.domain.AjaxResult;
import com.wei.common.core.page.TableDataInfo;
import com.wei.common.enums.system.BusinessType;
import com.wei.common.enums.hrm.StaffStatusEnum;
import com.wei.common.exception.BusinessException;
import com.wei.common.utils.StringUtils;
import com.wei.common.utils.poi.ExcelUtil;
import com.wei.hrm.common.HrmCheckUtils;
import com.wei.hrm.sm.domain.HrmInterviewRecord;
import com.wei.hrm.sm.domain.HrmStaff;
import com.wei.hrm.sm.dto.InterviewRecordDTO;
import com.wei.hrm.sm.service.IHrmInterviewRecordService;
import com.wei.hrm.sm.service.IHrmStaffService;
import com.wei.hrm.sm.state.StaffState;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 面试记录Controller
 *
 * @author wei
 * @date 2024-09-15
 */
@RestController
@RequestMapping("/sm/hrm-interview-record")
public class HrmInterviewRecordController extends BaseController {

    @Resource
    private IHrmInterviewRecordService hrmInterviewRecordService;

    @Resource
    private IHrmStaffService hrmStaffService;

    /**
     * 邀请面试
     */
    @PreAuthorize("@ss.hasPermi('sm:hrmstaff:edit')")
    @Log(title = "邀请面试", businessType = BusinessType.UPDATE)
    @PutMapping("/interview/{staffId}")
    public AjaxResult interview(@PathVariable Long staffId) {
        HrmStaff staff = this.hrmStaffService.getById(staffId);
        Optional.ofNullable(staff).orElseThrow(() -> new BusinessException(MsgConstants.PARAM_ERROR));
        StaffState state = StaffState.INTERVIEW;
        // 校验状态
        HrmCheckUtils.checkStaffStatus(state, staff.getStatus()).throwMsg(MsgConstants.ERROR_STATUS);
        // 邀请面试
        this.hrmStaffService.lambdaUpdate().eq(HrmStaff::getStaffId, staffId)
                .set(HrmStaff::getStatus, state.getCurrentStatus()).update(new HrmStaff());
        return AjaxResult.success();
    }

    /**
     * 查询面试记录列表
     */
    @PreAuthorize("@ss.hasPermi('sm:hrminterviewrecord:list')")
    @GetMapping("/list")
    public TableDataInfo list(HrmInterviewRecord hrmInterviewRecord) {
        startPage();
        List<InterviewRecordDTO> list = this.hrmInterviewRecordService.pageGroupByStaff(hrmInterviewRecord);
        return getDataTable(list);
    }

    /**
     * 导出面试记录列表
     */
    @PreAuthorize("@ss.hasPermi('sm:hrminterviewrecord:export')")
    @Log(title = "面试记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HrmInterviewRecord hrmInterviewRecord) {
        // 获取查询条件
        LambdaQueryWrapper<HrmInterviewRecord> queryWrapper = getQueryWrapper(hrmInterviewRecord);
        List<HrmInterviewRecord> list = this.hrmInterviewRecordService.list(queryWrapper);
        ExcelUtil<HrmInterviewRecord> util = new ExcelUtil<HrmInterviewRecord>(HrmInterviewRecord.class);
        util.exportExcel(response, list, "面试记录数据");
    }

    /**
     * 获取面试记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('sm:hrminterviewrecord:query')")
    @GetMapping(value = "/{interviewRecordId}")
    public AjaxResult getInfo(@PathVariable("interviewRecordId") Long interviewRecordId) {
        return AjaxResult.success(this.hrmInterviewRecordService.getById(interviewRecordId));
    }

    /**
     * 新增面试记录
     */
    @PreAuthorize("@ss.hasPermi('sm:hrminterviewrecord:add')")
    @Log(title = "面试记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HrmInterviewRecord hrmInterviewRecord) {
        Optional.ofNullable(hrmInterviewRecord.getTimeForInterview()).orElseThrow(() -> new BusinessException("面试时间不允许为空"));
        hrmInterviewRecord.setStatus(StaffStatusEnum.INTERVIEW.getCode().longValue());
        return toAjax(this.hrmInterviewRecordService.save(hrmInterviewRecord));
    }

    /**
     * 修改面试记录
     */
    @PreAuthorize("@ss.hasPermi('sm:hrminterviewrecord:edit')")
    @Log(title = "面试记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HrmInterviewRecord hrmInterviewRecord) {
        // 校验员工数据
        HrmStaff staff = this.hrmStaffService.getById(hrmInterviewRecord.getStaffId());
        Optional.ofNullable(staff).orElseThrow(() -> new BusinessException(MsgConstants.PARAM_ERROR));
        // 校验当前状态是否允许执行此操作
        StaffState state = StaffState.STAFF_STATE_MAP.get(hrmInterviewRecord.getStatus().intValue());
        HrmCheckUtils.checkStaffStatus(state, staff.getStatus()).throwMsg(MsgConstants.ERROR_STATUS);
        return toAjax(this.hrmInterviewRecordService.updateWithStaff(hrmInterviewRecord));
    }

    /**
     * 删除面试记录
     */
    @PreAuthorize("@ss.hasPermi('sm:hrminterviewrecord:remove')")
    @Log(title = "面试记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{interviewRecordIds}")
    public AjaxResult remove(@PathVariable List<Long> interviewRecordIds) {
        return toAjax(this.hrmInterviewRecordService.removeByIds(interviewRecordIds));
    }

    /**
     * 获取查询条件
     */
    public LambdaQueryWrapper<HrmInterviewRecord> getQueryWrapper(HrmInterviewRecord hrmInterviewRecord) {
        LambdaQueryWrapper<HrmInterviewRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(hrmInterviewRecord.getStaffCode() != null, HrmInterviewRecord::getStaffCode, hrmInterviewRecord.getStaffCode());
        queryWrapper.like(StringUtils.isNotEmpty(hrmInterviewRecord.getStaffName()), HrmInterviewRecord::getStaffName, hrmInterviewRecord.getStaffName());
        queryWrapper.like(StringUtils.isNotEmpty(hrmInterviewRecord.getInterviewerName()), HrmInterviewRecord::getInterviewerName, hrmInterviewRecord.getInterviewerName());
        // 默认创建时间倒序
        queryWrapper.orderByDesc(HrmInterviewRecord::getCreateTime);
        Map<String, Object> params = hrmInterviewRecord.getParams();
        queryWrapper.between(params.get("beginTime") != null && params.get("endTime") != null, HrmInterviewRecord::getCreateTime, params.get("beginTime"), params.get("endTime"));
        return queryWrapper;
    }
}
