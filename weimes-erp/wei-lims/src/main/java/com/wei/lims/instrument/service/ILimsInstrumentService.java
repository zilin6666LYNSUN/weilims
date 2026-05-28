package com.wei.lims.instrument.service;

import java.util.List;
import com.wei.lims.instrument.domain.LimsInstrument;

/**
 * 仪器管理Service接口
 */
public interface ILimsInstrumentService {

    public LimsInstrument selectLimsInstrumentByInstrumentId(Long instrumentId);

    public List<LimsInstrument> selectLimsInstrumentList(LimsInstrument limsInstrument);

    public int insertLimsInstrument(LimsInstrument limsInstrument);

    public int updateLimsInstrument(LimsInstrument limsInstrument);

    public int deleteLimsInstrumentByInstrumentIds(Long[] instrumentIds);

    public String checkInstrumentCodeUnique(LimsInstrument limsInstrument);

    /**
     * 校准到期提醒
     */
    public List<LimsInstrument> selectCalibrationDueList(int withinDays);
}
