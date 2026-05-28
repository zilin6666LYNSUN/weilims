package com.wei.lims.instrument.mapper;

import java.util.List;
import com.wei.lims.instrument.domain.LimsInstrument;

/**
 * 仪器台账Mapper接口
 */
public interface LimsInstrumentMapper {

    public LimsInstrument selectLimsInstrumentByInstrumentId(Long instrumentId);

    public LimsInstrument selectLimsInstrumentByInstrumentCode(String instrumentCode);

    public List<LimsInstrument> selectLimsInstrumentList(LimsInstrument limsInstrument);

    public int insertLimsInstrument(LimsInstrument limsInstrument);

    public int updateLimsInstrument(LimsInstrument limsInstrument);

    public int deleteLimsInstrumentByInstrumentIds(Long[] instrumentIds);

    public int deleteLimsInstrumentByInstrumentId(Long instrumentId);

    public String checkInstrumentCodeUnique(LimsInstrument limsInstrument);
}
