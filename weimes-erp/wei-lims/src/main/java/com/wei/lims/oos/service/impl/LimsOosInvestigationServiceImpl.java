package com.wei.lims.oos.service.impl;

import java.util.List;
import com.wei.common.utils.DateUtils;
import com.wei.lims.oos.domain.LimsOosInvestigation;
import com.wei.lims.oos.mapper.LimsOosInvestigationMapper;
import com.wei.lims.oos.service.ILimsOosInvestigationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LimsOosInvestigationServiceImpl implements ILimsOosInvestigationService {

    @Autowired
    private LimsOosInvestigationMapper limsOosInvestigationMapper;

    @Override
    public LimsOosInvestigation selectByInvestigationId(Long investigationId) {
        return limsOosInvestigationMapper.selectByInvestigationId(investigationId);
    }

    @Override
    public List<LimsOosInvestigation> selectLimsOosInvestigationList(LimsOosInvestigation investigation) {
        return limsOosInvestigationMapper.selectLimsOosInvestigationList(investigation);
    }

    @Override
    public int insertLimsOosInvestigation(LimsOosInvestigation investigation) {
        investigation.setCreateTime(DateUtils.getNowDate());
        return limsOosInvestigationMapper.insertLimsOosInvestigation(investigation);
    }

    @Override
    public int updateLimsOosInvestigation(LimsOosInvestigation investigation) {
        investigation.setUpdateTime(DateUtils.getNowDate());
        return limsOosInvestigationMapper.updateLimsOosInvestigation(investigation);
    }

    @Override
    public int deleteLimsOosInvestigationByIds(Long[] investigationIds) {
        return limsOosInvestigationMapper.deleteLimsOosInvestigationByIds(investigationIds);
    }

    @Override
    public int submitInvestigation(Long investigationId) {
        LimsOosInvestigation inv = limsOosInvestigationMapper.selectByInvestigationId(investigationId);
        inv.setStatus("INVESTIGATING");
        return limsOosInvestigationMapper.updateLimsOosInvestigation(inv);
    }

    @Override
    public int reviewInvestigation(Long investigationId) {
        LimsOosInvestigation inv = limsOosInvestigationMapper.selectByInvestigationId(investigationId);
        inv.setStatus("REVIEWING");
        return limsOosInvestigationMapper.updateLimsOosInvestigation(inv);
    }

    @Override
    public int approveInvestigation(Long investigationId) {
        LimsOosInvestigation inv = limsOosInvestigationMapper.selectByInvestigationId(investigationId);
        inv.setStatus("APPROVED");
        inv.setCloseDate(DateUtils.getNowDate());
        return limsOosInvestigationMapper.updateLimsOosInvestigation(inv);
    }
}
