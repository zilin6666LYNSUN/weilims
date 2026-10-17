package com.wei.lims.training.service.impl;

import java.util.List;
import com.wei.lims.training.domain.LimsStaffTestAuthorization;
import com.wei.lims.training.mapper.LimsStaffTestAuthorizationMapper;
import com.wei.lims.training.service.ILimsStaffTestAuthorizationService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsStaffTestAuthorizationServiceImpl implements ILimsStaffTestAuthorizationService {

    @Autowired
    private LimsStaffTestAuthorizationMapper limsstafftestauthorizationMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsStaffTestAuthorization selectLimsStaffTestAuthorizationByAuthId(Long authId) {
        return limsstafftestauthorizationMapper.selectLimsStaffTestAuthorizationByAuthId(authId);
    }

    @Override
    public List<LimsStaffTestAuthorization> selectLimsStaffTestAuthorizationList(LimsStaffTestAuthorization limsstafftestauthorization) {
        return limsstafftestauthorizationMapper.selectLimsStaffTestAuthorizationList(limsstafftestauthorization);
    }

    @Override
    @Transactional
    public int insertLimsStaffTestAuthorization(LimsStaffTestAuthorization limsstafftestauthorization) {
        int rows = limsstafftestauthorizationMapper.insertLimsStaffTestAuthorization(limsstafftestauthorization);
        limsAuditTrailService.recordFieldChanges("lims_staff_test_authorization", limsstafftestauthorization.getAuthId(), "INSERT", null, limsstafftestauthorization, "新建人员检测项授权");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsStaffTestAuthorization(LimsStaffTestAuthorization limsstafftestauthorization) {
        LimsStaffTestAuthorization old = limsstafftestauthorizationMapper.selectLimsStaffTestAuthorizationByAuthId(limsstafftestauthorization.getAuthId());
        int rows = limsstafftestauthorizationMapper.updateLimsStaffTestAuthorization(limsstafftestauthorization);
        limsAuditTrailService.recordFieldChanges("lims_staff_test_authorization", limsstafftestauthorization.getAuthId(), "UPDATE", old, limsstafftestauthorization, "更新人员检测项授权");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsStaffTestAuthorizationByAuthId(Long[] authIds) {
        for (Long authId : authIds) {
            LimsStaffTestAuthorization old = limsstafftestauthorizationMapper.selectLimsStaffTestAuthorizationByAuthId(authId);
            limsAuditTrailService.recordFieldChanges("lims_staff_test_authorization", authId, "DELETE", old, null, "删除人员检测项授权");
        }
        return limsstafftestauthorizationMapper.deleteLimsStaffTestAuthorizationByAuthId(authIds);
    }
}