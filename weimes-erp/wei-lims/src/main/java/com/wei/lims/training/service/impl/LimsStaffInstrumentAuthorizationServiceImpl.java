package com.wei.lims.training.service.impl;

import java.util.List;
import com.wei.lims.training.domain.LimsStaffInstrumentAuthorization;
import com.wei.lims.training.mapper.LimsStaffInstrumentAuthorizationMapper;
import com.wei.lims.training.service.ILimsStaffInstrumentAuthorizationService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsStaffInstrumentAuthorizationServiceImpl implements ILimsStaffInstrumentAuthorizationService {

    @Autowired
    private LimsStaffInstrumentAuthorizationMapper limsstaffinstrumentauthorizationMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsStaffInstrumentAuthorization selectLimsStaffInstrumentAuthorizationByAuthId(Long authId) {
        return limsstaffinstrumentauthorizationMapper.selectLimsStaffInstrumentAuthorizationByAuthId(authId);
    }

    @Override
    public List<LimsStaffInstrumentAuthorization> selectLimsStaffInstrumentAuthorizationList(LimsStaffInstrumentAuthorization limsstaffinstrumentauthorization) {
        return limsstaffinstrumentauthorizationMapper.selectLimsStaffInstrumentAuthorizationList(limsstaffinstrumentauthorization);
    }

    @Override
    @Transactional
    public int insertLimsStaffInstrumentAuthorization(LimsStaffInstrumentAuthorization limsstaffinstrumentauthorization) {
        int rows = limsstaffinstrumentauthorizationMapper.insertLimsStaffInstrumentAuthorization(limsstaffinstrumentauthorization);
        limsAuditTrailService.recordFieldChanges("lims_staff_instrument_authorization", limsstaffinstrumentauthorization.getAuthId(), "INSERT", null, limsstaffinstrumentauthorization, "新建人员仪器授权");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsStaffInstrumentAuthorization(LimsStaffInstrumentAuthorization limsstaffinstrumentauthorization) {
        LimsStaffInstrumentAuthorization old = limsstaffinstrumentauthorizationMapper.selectLimsStaffInstrumentAuthorizationByAuthId(limsstaffinstrumentauthorization.getAuthId());
        int rows = limsstaffinstrumentauthorizationMapper.updateLimsStaffInstrumentAuthorization(limsstaffinstrumentauthorization);
        limsAuditTrailService.recordFieldChanges("lims_staff_instrument_authorization", limsstaffinstrumentauthorization.getAuthId(), "UPDATE", old, limsstaffinstrumentauthorization, "更新人员仪器授权");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsStaffInstrumentAuthorizationByAuthId(Long[] authIds) {
        for (Long authId : authIds) {
            LimsStaffInstrumentAuthorization old = limsstaffinstrumentauthorizationMapper.selectLimsStaffInstrumentAuthorizationByAuthId(authId);
            limsAuditTrailService.recordFieldChanges("lims_staff_instrument_authorization", authId, "DELETE", old, null, "删除人员仪器授权");
        }
        return limsstaffinstrumentauthorizationMapper.deleteLimsStaffInstrumentAuthorizationByAuthId(authIds);
    }
}