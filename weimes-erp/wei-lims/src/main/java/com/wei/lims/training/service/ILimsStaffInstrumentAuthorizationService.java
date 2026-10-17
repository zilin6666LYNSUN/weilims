package com.wei.lims.training.service;

import java.util.List;
import com.wei.lims.training.domain.LimsStaffInstrumentAuthorization;

public interface ILimsStaffInstrumentAuthorizationService {
    public LimsStaffInstrumentAuthorization selectLimsStaffInstrumentAuthorizationByAuthId(Long authId);
    public List<LimsStaffInstrumentAuthorization> selectLimsStaffInstrumentAuthorizationList(LimsStaffInstrumentAuthorization LimsStaffInstrumentAuthorization);
    public int insertLimsStaffInstrumentAuthorization(LimsStaffInstrumentAuthorization limsstaffinstrumentauthorization);
    public int updateLimsStaffInstrumentAuthorization(LimsStaffInstrumentAuthorization limsstaffinstrumentauthorization);
    public int deleteLimsStaffInstrumentAuthorizationByAuthId(Long[] authIds);
}