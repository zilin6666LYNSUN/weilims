package com.wei.lims.instrument.service.impl;

import java.util.List;
import com.wei.common.utils.DateUtils;
import com.wei.lims.instrument.domain.LimsInstrument;
import com.wei.lims.instrument.mapper.LimsInstrumentMapper;
import com.wei.lims.instrument.service.ILimsInstrumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LimsInstrumentServiceImpl implements ILimsInstrumentService {

    @Autowired
    private LimsInstrumentMapper limsInstrumentMapper;

    @Override
    public LimsInstrument selectLimsInstrumentByInstrumentId(Long instrumentId) {
        return limsInstrumentMapper.selectLimsInstrumentByInstrumentId(instrumentId);
    }

    @Override
    public List<LimsInstrument> selectLimsInstrumentList(LimsInstrument limsInstrument) {
        return limsInstrumentMapper.selectLimsInstrumentList(limsInstrument);
    }

    @Override
    public int insertLimsInstrument(LimsInstrument limsInstrument) {
        limsInstrument.setCreateTime(DateUtils.getNowDate());
        return limsInstrumentMapper.insertLimsInstrument(limsInstrument);
    }

    @Override
    public int updateLimsInstrument(LimsInstrument limsInstrument) {
        limsInstrument.setUpdateTime(DateUtils.getNowDate());
        return limsInstrumentMapper.updateLimsInstrument(limsInstrument);
    }

    @Override
    public int deleteLimsInstrumentByInstrumentIds(Long[] instrumentIds) {
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
