package com.wei.lims.instrument.service.impl;

import java.util.List;
import com.wei.common.utils.DateUtils;
import com.wei.lims.instrument.domain.LimsInstrumentCalibration;
import com.wei.lims.instrument.mapper.LimsInstrumentCalibrationMapper;
import com.wei.lims.instrument.service.ILimsInstrumentCalibrationService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsInstrumentCalibrationServiceImpl implements ILimsInstrumentCalibrationService {

    @Autowired
    private LimsInstrumentCalibrationMapper limsInstrumentCalibrationMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

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
    @Transactional
    public int insertLimsInstrumentCalibration(LimsInstrumentCalibration calibration) {
        calibration.setCreateTime(DateUtils.getNowDate());
        int rows = limsInstrumentCalibrationMapper.insertLimsInstrumentCalibration(calibration);
        limsAuditTrailService.recordFieldChanges("lims_instrument_calibration", calibration.getCalibrationId(),
                "INSERT", null, calibration, "新建仪器校准");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsInstrumentCalibration(LimsInstrumentCalibration calibration) {
        LimsInstrumentCalibration oldCalibration = limsInstrumentCalibrationMapper.selectByCalibrationId(calibration.getCalibrationId());
        calibration.setUpdateTime(DateUtils.getNowDate());
        int rows = limsInstrumentCalibrationMapper.updateLimsInstrumentCalibration(calibration);
        limsAuditTrailService.recordFieldChanges("lims_instrument_calibration", calibration.getCalibrationId(),
                "UPDATE", oldCalibration, calibration, "更新仪器校准");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsInstrumentCalibrationByIds(Long[] calibrationIds) {
        for (Long calibrationId : calibrationIds) {
            limsAuditTrailService.recordFieldChanges("lims_instrument_calibration", calibrationId,
                    "DELETE", null, null, "删除仪器校准");
        }
        return limsInstrumentCalibrationMapper.deleteLimsInstrumentCalibrationByIds(calibrationIds);
    }
}
