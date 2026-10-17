package com.wei.lims.training.mapper;

import java.util.List;
import com.wei.lims.training.domain.LimsStaffTestAuthorization;

public interface LimsStaffTestAuthorizationMapper {
    public LimsStaffTestAuthorization selectLimsStaffTestAuthorizationByAuthId(Long authId);
    public List<LimsStaffTestAuthorization> selectLimsStaffTestAuthorizationList(LimsStaffTestAuthorization LimsStaffTestAuthorization);
    public int insertLimsStaffTestAuthorization(LimsStaffTestAuthorization limsstafftestauthorization);
    public int updateLimsStaffTestAuthorization(LimsStaffTestAuthorization limsstafftestauthorization);
    public int deleteLimsStaffTestAuthorizationByAuthId(Long[] authIds);
}