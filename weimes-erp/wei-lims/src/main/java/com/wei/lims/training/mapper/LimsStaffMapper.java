package com.wei.lims.training.mapper;

import java.util.List;
import com.wei.lims.training.domain.LimsStaff;

public interface LimsStaffMapper {
    public LimsStaff selectLimsStaffByStaffId(Long staffId);
    public List<LimsStaff> selectLimsStaffList(LimsStaff LimsStaff);
    public int insertLimsStaff(LimsStaff limsstaff);
    public int updateLimsStaff(LimsStaff limsstaff);
    public int deleteLimsStaffByStaffId(Long[] staffIds);
}