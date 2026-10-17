package com.wei.lims.oos.service.impl;

import java.util.List;
import com.wei.common.utils.DateUtils;
import com.wei.lims.oos.domain.LimsOosInvestigation;
import com.wei.lims.oos.mapper.LimsOosInvestigationMapper;
import com.wei.lims.oos.service.ILimsOosInvestigationService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsOosInvestigationServiceImpl implements ILimsOosInvestigationService {

    @Autowired
    private LimsOosInvestigationMapper limsOosInvestigationMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsOosInvestigation selectByInvestigationId(Long investigationId) {
        return limsOosInvestigationMapper.selectByInvestigationId(investigationId);
    }

    @Override
    public List<LimsOosInvestigation> selectLimsOosInvestigationList(LimsOosInvestigation investigation) {
        return limsOosInvestigationMapper.selectLimsOosInvestigationList(investigation);
    }

    @Override
    @Transactional
    public int insertLimsOosInvestigation(LimsOosInvestigation investigation) {
        investigation.setCreateTime(DateUtils.getNowDate());
        int rows = limsOosInvestigationMapper.insertLimsOosInvestigation(investigation);
        limsAuditTrailService.recordFieldChanges("lims_oos_investigation", investigation.getInvestigationId(),
                "INSERT", null, investigation, "新建OOS调查");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsOosInvestigation(LimsOosInvestigation investigation) {
        LimsOosInvestigation oldInvestigation = limsOosInvestigationMapper.selectByInvestigationId(investigation.getInvestigationId());
        investigation.setUpdateTime(DateUtils.getNowDate());
        int rows = limsOosInvestigationMapper.updateLimsOosInvestigation(investigation);
        limsAuditTrailService.recordFieldChanges("lims_oos_investigation", investigation.getInvestigationId(),
                "UPDATE", oldInvestigation, investigation, "更新OOS调查");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsOosInvestigationByIds(Long[] investigationIds) {
        for (Long investigationId : investigationIds) {
            limsAuditTrailService.recordFieldChanges("lims_oos_investigation", investigationId,
                    "DELETE", null, null, "删除OOS调查");
        }
        return limsOosInvestigationMapper.deleteLimsOosInvestigationByIds(investigationIds);
    }

    @Override
    @Transactional
    public int submitInvestigation(Long investigationId) {
        LimsOosInvestigation oldInv = limsOosInvestigationMapper.selectByInvestigationId(investigationId);
        LimsOosInvestigation newInv = limsOosInvestigationMapper.selectByInvestigationId(investigationId);
        newInv.setStatus("INVESTIGATING");
        int rows = limsOosInvestigationMapper.updateLimsOosInvestigation(newInv);
        limsAuditTrailService.recordFieldChanges("lims_oos_investigation", investigationId,
                "UPDATE", oldInv, newInv, "提交OOS调查");
        return rows;
    }

    @Override
    @Transactional
    public int reviewInvestigation(Long investigationId) {
        LimsOosInvestigation oldInv = limsOosInvestigationMapper.selectByInvestigationId(investigationId);
        LimsOosInvestigation newInv = limsOosInvestigationMapper.selectByInvestigationId(investigationId);
        newInv.setStatus("REVIEWING");
        int rows = limsOosInvestigationMapper.updateLimsOosInvestigation(newInv);
        limsAuditTrailService.recordFieldChanges("lims_oos_investigation", investigationId,
                "UPDATE", oldInv, newInv, "复核OOS调查");
        return rows;
    }

    @Override
    @Transactional
    public int approveInvestigation(Long investigationId) {
        LimsOosInvestigation oldInv = limsOosInvestigationMapper.selectByInvestigationId(investigationId);
        LimsOosInvestigation newInv = limsOosInvestigationMapper.selectByInvestigationId(investigationId);
        newInv.setStatus("APPROVED");
        newInv.setCloseDate(DateUtils.getNowDate());
        int rows = limsOosInvestigationMapper.updateLimsOosInvestigation(newInv);
        limsAuditTrailService.recordFieldChanges("lims_oos_investigation", investigationId,
                "UPDATE", oldInv, newInv, "审批OOS调查");
        return rows;
    }
}
