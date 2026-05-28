package com.wei.lims.instrument.service;

import java.util.List;
import com.wei.lims.instrument.domain.LimsInstrumentCalibration;

public interface ILimsInstrumentCalibrationService {
    public LimsInstrumentCalibration selectByCalibrationId(Long calibrationId);
    public List<LimsInstrumentCalibration> selectByInstrumentId(Long instrumentId);
    public List<LimsInstrumentCalibration> selectLimsInstrumentCalibrationList(LimsInstrumentCalibration calibration);
    public int insertLimsInstrumentCalibration(LimsInstrumentCalibration calibration);
    public int updateLimsInstrumentCalibration(LimsInstrumentCalibration calibration);
    public int deleteLimsInstrumentCalibrationByIds(Long[] calibrationIds);
}
