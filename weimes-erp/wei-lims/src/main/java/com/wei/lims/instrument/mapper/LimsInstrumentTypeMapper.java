package com.wei.lims.instrument.mapper;

import java.util.List;
import com.wei.lims.instrument.domain.LimsInstrumentType;

public interface LimsInstrumentTypeMapper {

    public LimsInstrumentType selectByTypeId(Long typeId);
    public LimsInstrumentType selectByTypeCode(String typeCode);
    public List<LimsInstrumentType> selectLimsInstrumentTypeList(LimsInstrumentType type);
    public int insertLimsInstrumentType(LimsInstrumentType type);
    public int updateLimsInstrumentType(LimsInstrumentType type);
    public int deleteLimsInstrumentTypeByIds(Long[] typeIds);
}
