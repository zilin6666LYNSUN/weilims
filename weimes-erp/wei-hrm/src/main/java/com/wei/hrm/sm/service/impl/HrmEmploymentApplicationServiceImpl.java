package com.wei.hrm.sm.service.impl;

import cn.hutool.extra.pinyin.PinyinUtil;
import com.wei.common.constant.MsgConstants;
import com.wei.common.constant.UserConstants;
import com.wei.common.core.domain.entity.SysUser;
import com.wei.common.enums.system.SexEnum;
import com.wei.common.enums.system.StatusEnum;
import com.wei.common.exception.BusinessException;
import com.wei.common.utils.SecurityUtils;
import com.wei.hrm.sm.domain.HrmInterviewRecord;
import com.wei.hrm.sm.domain.HrmStaff;
import com.wei.hrm.sm.service.IHrmEmploymentApplicationService;
import com.wei.hrm.sm.service.IHrmInterviewRecordService;
import com.wei.hrm.sm.service.IHrmStaffService;
import com.wei.hrm.sm.state.StaffState;
import com.wei.system.service.ISysUserService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

/**
 * 入职申请
 *
 * @author wei
 * @date 2024/9/22 21:20
 */
@Service
public class HrmEmploymentApplicationServiceImpl implements IHrmEmploymentApplicationService {

    @Resource
    private IHrmInterviewRecordService interviewRecordService;
    @Resource
    private IHrmStaffService hrmStaffService;
    @Resource
    private ISysUserService sysUserService;

    /**
     * 入职申请
     */
    @Transactional
    @Override
    public void employmentApplication(HrmInterviewRecord hrmInterviewRecord, StaffState state) {
        // 更新面试申请表
        this.interviewRecordService
                .lambdaUpdate()
                .set(HrmInterviewRecord::getActualSalary, hrmInterviewRecord.getActualSalary())
                .eq(HrmInterviewRecord::getInterviewRecordId, hrmInterviewRecord.getInterviewRecordId())
                .update(new HrmInterviewRecord());
        // 更新员工表
        this.hrmStaffService.employmentApplication(hrmInterviewRecord, state);
    }

    /**
     * 审核通过
     */
    @Transactional
    @Override
    public String pass(HrmInterviewRecord hrmInterviewRecord, StaffState state) {
        // 写入新的面试记录
        HrmInterviewRecord newRecord = saveNewInterviewRecord(hrmInterviewRecord, state);
        this.interviewRecordService.save(newRecord);
        // 创建该员工的账户
        SysUser user = buildSysUser(hrmInterviewRecord.getStaffId(), hrmInterviewRecord);
        this.sysUserService.insertUser(user);
        // 修改员工状态
        this.hrmStaffService.lambdaUpdate()
                .set(HrmStaff::getStatus, state.getCurrentStatus())
                .eq(HrmStaff::getStaffId, hrmInterviewRecord.getStaffId())
                .update(new HrmStaff());
        return "当前已生成员工账号";
    }

    /**
     * 构建用户信息
     */
    private SysUser buildSysUser(Long staffId, HrmInterviewRecord hrmInterviewRecord) {
        HrmStaff staff = this.hrmStaffService.getById(staffId);
        String account = PinyinUtil.getFirstLetter(staff.getStaffName(), "") + StringUtils.right(staff.getContactPhone(), 4);
        SysUser user = new SysUser();
        user.setUserId(staffId);
        user.setUserName(account);
        user.setNickName(staff.getStaffName());
        user.setPassword(SecurityUtils.encryptPassword(UserConstants.DEFAULT_PASSWORD));
        user.setPhonenumber(staff.getContactPhone());
        user.setSex(staff.getSex() != null ? staff.getSex().toString() : SexEnum.UNKNOWN.getCode());
        user.setEmail(staff.getEmail());
        user.setStatus(StatusEnum.ENABLE.getCode());
        user.setRoleIds(new Long[]{hrmInterviewRecord.getRoleId()});
        user.setPostIds(new Long[]{hrmInterviewRecord.getPostId()});
        user.setDeptId(hrmInterviewRecord.getDeptId());
        user.setCreateBy(SecurityUtils.getUserId().toString());
        user.setCreateTime(new Date());
        return user;
    }

    /**
     * 新面试记录
     */
    private HrmInterviewRecord saveNewInterviewRecord(HrmInterviewRecord hrmInterviewRecord, StaffState state) {
        HrmInterviewRecord newRecord = this.interviewRecordService.getById(hrmInterviewRecord.getInterviewRecordId());
        Optional.ofNullable(newRecord).orElseThrow(() -> new BusinessException(MsgConstants.ERROR_DATA));
        // 写入新纪录
        newRecord.setRemark(hrmInterviewRecord.getRemark());
        newRecord.setInterviewRecordId(null);
        newRecord.setStatus(state.getCurrentStatus().longValue());
        return newRecord;
    }
}
