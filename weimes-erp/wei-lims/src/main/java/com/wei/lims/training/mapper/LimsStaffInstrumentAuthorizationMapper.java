package com.wei.lims.training.mapper;

import java.util.List;
import com.wei.lims.training.domain.LimsStaffInstrumentAuthorization;

public interface LimsStaffInstrumentAuthorizationMapper {
    public LimsStaffInstrumentAuthorization selectLimsStaffInstrumentAuthorizationByAuthId(Long authId);
    public List<LimsStaffInstrumentAuthorization> selectLimsStaffInstrumentAuthorizationList(LimsStaffInstrumentAuthorization LimsStaffInstrumentAuthorization);
    public int insertLimsStaffInstrumentAuthorization(LimsStaffInstrumentAuthorization limsstaffinstrumentauthorization);
    public int updateLimsStaffInstrumentAuthorization(LimsStaffInstrumentAuthorization limsstaffinstrumentauthorization);
    public int deleteLimsStaffInstrumentAuthorizationByAuthId(Long[] authIds);
}