package com.wei.lims.instrument.service.impl;

import java.util.List;
import com.wei.lims.instrument.domain.LimsInstrumentType;
import com.wei.lims.instrument.mapper.LimsInstrumentTypeMapper;
import com.wei.lims.instrument.service.ILimsInstrumentTypeService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsInstrumentTypeServiceImpl implements ILimsInstrumentTypeService {

    @Autowired
    private LimsInstrumentTypeMapper limsInstrumentTypeMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsInstrumentType selectByTypeId(Long typeId) {
        return limsInstrumentTypeMapper.selectByTypeId(typeId);
    }

    @Override
    public List<LimsInstrumentType> selectLimsInstrumentTypeList(LimsInstrumentType type) {
        return limsInstrumentTypeMapper.selectLimsInstrumentTypeList(type);
    }

    @Override
    @Transactional
    public int insertLimsInstrumentType(LimsInstrumentType type) {
        int rows = limsInstrumentTypeMapper.insertLimsInstrumentType(type);
        limsAuditTrailService.recordFieldChanges("lims_instrument_type", type.getTypeId(),
                "INSERT", null, type, "新建仪器类型");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsInstrumentType(LimsInstrumentType type) {
        LimsInstrumentType oldType = limsInstrumentTypeMapper.selectByTypeId(type.getTypeId());
        int rows = limsInstrumentTypeMapper.updateLimsInstrumentType(type);
        limsAuditTrailService.recordFieldChanges("lims_instrument_type", type.getTypeId(),
                "UPDATE", oldType, type, "更新仪器类型");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsInstrumentTypeByIds(Long[] typeIds) {
        for (Long typeId : typeIds) {
            LimsInstrumentType oldType = limsInstrumentTypeMapper.selectByTypeId(typeId);
            limsAuditTrailService.recordFieldChanges("lims_instrument_type", typeId,
                    "DELETE", oldType, null, "删除仪器类型");
        }
        return limsInstrumentTypeMapper.deleteLimsInstrumentTypeByIds(typeIds);
    }
}
