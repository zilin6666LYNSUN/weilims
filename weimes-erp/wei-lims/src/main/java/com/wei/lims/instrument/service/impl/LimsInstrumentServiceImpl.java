package com.wei.lims.instrument.service.impl;

import java.util.List;
import com.wei.common.utils.DateUtils;
import com.wei.lims.instrument.domain.LimsInstrument;
import com.wei.lims.instrument.mapper.LimsInstrumentMapper;
import com.wei.lims.instrument.service.ILimsInstrumentService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsInstrumentServiceImpl implements ILimsInstrumentService {

    @Autowired
    private LimsInstrumentMapper limsInstrumentMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsInstrument selectLimsInstrumentByInstrumentId(Long instrumentId) {
        return limsInstrumentMapper.selectLimsInstrumentByInstrumentId(instrumentId);
    }

    @Override
    public List<LimsInstrument> selectLimsInstrumentList(LimsInstrument limsInstrument) {
        return limsInstrumentMapper.selectLimsInstrumentList(limsInstrument);
    }

    @Override
    @Transactional
    public int insertLimsInstrument(LimsInstrument limsInstrument) {
        limsInstrument.setCreateTime(DateUtils.getNowDate());
        int rows = limsInstrumentMapper.insertLimsInstrument(limsInstrument);
        limsAuditTrailService.recordFieldChanges("lims_instrument", limsInstrument.getInstrumentId(),
                "INSERT", null, limsInstrument, "新建仪器");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsInstrument(LimsInstrument limsInstrument) {
        LimsInstrument oldInstrument = limsInstrumentMapper.selectLimsInstrumentByInstrumentId(limsInstrument.getInstrumentId());
        limsInstrument.setUpdateTime(DateUtils.getNowDate());
        int rows = limsInstrumentMapper.updateLimsInstrument(limsInstrument);
        limsAuditTrailService.recordFieldChanges("lims_instrument", limsInstrument.getInstrumentId(),
                "UPDATE", oldInstrument, limsInstrument, "更新仪器");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsInstrumentByInstrumentIds(Long[] instrumentIds) {
        for (Long instrumentId : instrumentIds) {
            limsAuditTrailService.recordFieldChanges("lims_instrument", instrumentId,
                    "DELETE", null, null, "删除仪器");
        }
        return limsInstrumentMapper.deleteLimsInstrumentByInstrumentIds(instrumentIds);
    }

    @Override
    public String checkInstrumentCodeUnique(LimsInstrument limsInstrument) {
        LimsInstrument existing = limsInstrumentMapper.selectLimsInstrumentByInstrumentCode(limsInstrument.getInstrumentCode());
        if (existing != null && !existing.getInstrumentId().equals(limsInstrument.getInstrumentId())) {
            return "1";
        }
        return "0";
    }

    @Override
    public List<LimsInstrument> selectCalibrationDueList(int withinDays) {
        LimsInstrument query = new LimsInstrument();
        return limsInstrumentMapper.selectLimsInstrumentList(query);
    }
}
