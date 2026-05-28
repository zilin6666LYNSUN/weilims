package com.wei.lims.instrument.service;

import java.util.List;
import com.wei.lims.instrument.domain.LimsInstrumentType;

public interface ILimsInstrumentTypeService {
    public LimsInstrumentType selectByTypeId(Long typeId);
    public List<LimsInstrumentType> selectLimsInstrumentTypeList(LimsInstrumentType type);
    public int insertLimsInstrumentType(LimsInstrumentType type);
    public int updateLimsInstrumentType(LimsInstrumentType type);
    public int deleteLimsInstrumentTypeByIds(Long[] typeIds);
}
