package com.wei.lims.instrument.mapper;

import java.util.List;
import com.wei.lims.instrument.domain.LimsInstrumentCalibration;

public interface LimsInstrumentCalibrationMapper {

    public LimsInstrumentCalibration selectByCalibrationId(Long calibrationId);
    public List<LimsInstrumentCalibration> selectByInstrumentId(Long instrumentId);
    public List<LimsInstrumentCalibration> selectLimsInstrumentCalibrationList(LimsInstrumentCalibration calibration);
    public int insertLimsInstrumentCalibration(LimsInstrumentCalibration calibration);
    public int updateLimsInstrumentCalibration(LimsInstrumentCalibration calibration);
    public int deleteLimsInstrumentCalibrationByIds(Long[] calibrationIds);
}
