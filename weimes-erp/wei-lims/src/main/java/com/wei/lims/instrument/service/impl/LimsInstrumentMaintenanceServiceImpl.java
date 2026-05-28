package com.wei.lims.instrument.service.impl;

import java.util.List;
import com.wei.lims.instrument.domain.LimsInstrumentMaintenance;
import com.wei.lims.instrument.mapper.LimsInstrumentMaintenanceMapper;
import com.wei.lims.instrument.service.ILimsInstrumentMaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LimsInstrumentMaintenanceServiceImpl implements ILimsInstrumentMaintenanceService {

    @Autowired
    private LimsInstrumentMaintenanceMapper limsInstrumentMaintenanceMapper;

    @Override
    public LimsInstrumentMaintenance selectByMaintenanceId(Long maintenanceId) {
        return limsInstrumentMaintenanceMapper.selectByMaintenanceId(maintenanceId);
    }

    @Override
    public List<LimsInstrumentMaintenance> selectByInstrumentId(Long instrumentId) {
        return limsInstrumentMaintenanceMapper.selectByInstrumentId(instrumentId);
    }

    @Override
    public List<LimsInstrumentMaintenance> selectLimsInstrumentMaintenanceList(LimsInstrumentMaintenance maintenance) {
        return limsInstrumentMaintenanceMapper.selectLimsInstrumentMaintenanceList(maintenance);
    }

    @Override
    public int insertLimsInstrumentMaintenance(LimsInstrumentMaintenance maintenance) {
        return limsInstrumentMaintenanceMapper.insertLimsInstrumentMaintenance(maintenance);
    }

    @Override
    public int updateLimsInstrumentMaintenance(LimsInstrumentMaintenance maintenance) {
        return limsInstrumentMaintenanceMapper.updateLimsInstrumentMaintenance(maintenance);
    }

    @Override
    public int deleteLimsInstrumentMaintenanceByIds(Long[] maintenanceIds) {
        return limsInstrumentMaintenanceMapper.deleteLimsInstrumentMaintenanceByIds(maintenanceIds);
    }
}
