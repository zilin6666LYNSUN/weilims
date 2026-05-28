package com.wei.lims.instrument.service.impl;

import java.util.List;
import com.wei.common.utils.DateUtils;
import com.wei.lims.instrument.domain.LimsInstrumentCalibration;
import com.wei.lims.instrument.mapper.LimsInstrumentCalibrationMapper;
import com.wei.lims.instrument.service.ILimsInstrumentCalibrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LimsInstrumentCalibrationServiceImpl implements ILimsInstrumentCalibrationService {

    @Autowired
    private LimsInstrumentCalibrationMapper limsInstrumentCalibrationMapper;

    @Override
    public LimsInstrumentCalibration selectByCalibrationId(Long calibrationId) {
        return limsInstrumentCalibrationMapper.selectByCalibrationId(calibrationId);
    }

    @Override
    public List<LimsInstrumentCalibration> selectByInstrumentId(Long instrumentId) {
        return limsInstrumentCalibrationMapper.selectByInstrumentId(instrumentId);
    }

    @Override
    public List<LimsInstrumentCalibration> selectLimsInstrumentCalibrationList(LimsInstrumentCalibration calibration) {
        return limsInstrumentCalibrationMapper.selectLimsInstrumentCalibrationList(calibration);
    }

    @Override
    public int insertLimsInstrumentCalibration(LimsInstrumentCalibration calibration) {
        calibration.setCreateTime(DateUtils.getNowDate());
        return limsInstrumentCalibrationMapper.insertLimsInstrumentCalibration(calibration);
    }

    @Override
    public int updateLimsInstrumentCalibration(LimsInstrumentCalibration calibration) {
        calibration.setUpdateTime(DateUtils.getNowDate());
        return limsInstrumentCalibrationMapper.updateLimsInstrumentCalibration(calibration);
    }

    @Override
    public int deleteLimsInstrumentCalibrationByIds(Long[] calibrationIds) {
        return limsInstrumentCalibrationMapper.deleteLimsInstrumentCalibrationByIds(calibrationIds);
    }
}
