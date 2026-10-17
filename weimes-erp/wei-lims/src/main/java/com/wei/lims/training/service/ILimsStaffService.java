package com.wei.lims.training.service;

import java.util.List;
import com.wei.lims.training.domain.LimsStaff;

public interface ILimsStaffService {
    public LimsStaff selectLimsStaffByStaffId(Long staffId);
    public List<LimsStaff> selectLimsStaffList(LimsStaff LimsStaff);
    public int insertLimsStaff(LimsStaff limsstaff);
    public int updateLimsStaff(LimsStaff limsstaff);
    public int deleteLimsStaffByStaffId(Long[] staffIds);
}