package com.wei.lims.training.service.impl;

import java.util.List;
import com.wei.lims.training.domain.LimsStaff;
import com.wei.lims.training.mapper.LimsStaffMapper;
import com.wei.lims.training.service.ILimsStaffService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsStaffServiceImpl implements ILimsStaffService {

    @Autowired
    private LimsStaffMapper limsstaffMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsStaff selectLimsStaffByStaffId(Long staffId) {
        return limsstaffMapper.selectLimsStaffByStaffId(staffId);
    }

    @Override
    public List<LimsStaff> selectLimsStaffList(LimsStaff limsstaff) {
        return limsstaffMapper.selectLimsStaffList(limsstaff);
    }

    @Override
    @Transactional
    public int insertLimsStaff(LimsStaff limsstaff) {
        int rows = limsstaffMapper.insertLimsStaff(limsstaff);
        limsAuditTrailService.recordFieldChanges("lims_staff", limsstaff.getStaffId(), "INSERT", null, limsstaff, "新建人员档案");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsStaff(LimsStaff limsstaff) {
        LimsStaff old = limsstaffMapper.selectLimsStaffByStaffId(limsstaff.getStaffId());
        int rows = limsstaffMapper.updateLimsStaff(limsstaff);
        limsAuditTrailService.recordFieldChanges("lims_staff", limsstaff.getStaffId(), "UPDATE", old, limsstaff, "更新人员档案");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsStaffByStaffId(Long[] staffIds) {
        for (Long staffId : staffIds) {
            LimsStaff old = limsstaffMapper.selectLimsStaffByStaffId(staffId);
            limsAuditTrailService.recordFieldChanges("lims_staff", staffId, "DELETE", old, null, "删除人员档案");
        }
        return limsstaffMapper.deleteLimsStaffByStaffId(staffIds);
    }
}