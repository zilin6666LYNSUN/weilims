package com.wei.lims.instrument.service.impl;

import java.util.List;
import com.wei.lims.instrument.domain.LimsInstrumentType;
import com.wei.lims.instrument.mapper.LimsInstrumentTypeMapper;
import com.wei.lims.instrument.service.ILimsInstrumentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LimsInstrumentTypeServiceImpl implements ILimsInstrumentTypeService {

    @Autowired
    private LimsInstrumentTypeMapper limsInstrumentTypeMapper;

    @Override
    public LimsInstrumentType selectByTypeId(Long typeId) {
        return limsInstrumentTypeMapper.selectByTypeId(typeId);
    }

    @Override
    public List<LimsInstrumentType> selectLimsInstrumentTypeList(LimsInstrumentType type) {
        return limsInstrumentTypeMapper.selectLimsInstrumentTypeList(type);
    }

    @Override
    public int insertLimsInstrumentType(LimsInstrumentType type) {
        return limsInstrumentTypeMapper.insertLimsInstrumentType(type);
    }

    @Override
    public int updateLimsInstrumentType(LimsInstrumentType type) {
        return limsInstrumentTypeMapper.updateLimsInstrumentType(type);
    }

    @Override
    public int deleteLimsInstrumentTypeByIds(Long[] typeIds) {
        return limsInstrumentTypeMapper.deleteLimsInstrumentTypeByIds(typeIds);
    }
}
