package com.wei.lims.instrument.service.impl;

import java.util.List;
import com.wei.lims.instrument.domain.LimsInstrumentMaintenance;
import com.wei.lims.instrument.mapper.LimsInstrumentMaintenanceMapper;
import com.wei.lims.instrument.service.ILimsInstrumentMaintenanceService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsInstrumentMaintenanceServiceImpl implements ILimsInstrumentMaintenanceService {

    @Autowired
    private LimsInstrumentMaintenanceMapper limsInstrumentMaintenanceMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

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
    @Transactional
    public int insertLimsInstrumentMaintenance(LimsInstrumentMaintenance maintenance) {
        int rows = limsInstrumentMaintenanceMapper.insertLimsInstrumentMaintenance(maintenance);
        limsAuditTrailService.recordFieldChanges("lims_instrument_maintenance", maintenance.getMaintenanceId(),
                "INSERT", null, maintenance, "新建仪器维护");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsInstrumentMaintenance(LimsInstrumentMaintenance maintenance) {
        LimsInstrumentMaintenance oldMaintenance = limsInstrumentMaintenanceMapper.selectByMaintenanceId(maintenance.getMaintenanceId());
        int rows = limsInstrumentMaintenanceMapper.updateLimsInstrumentMaintenance(maintenance);
        limsAuditTrailService.recordFieldChanges("lims_instrument_maintenance", maintenance.getMaintenanceId(),
                "UPDATE", oldMaintenance, maintenance, "更新仪器维护");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsInstrumentMaintenanceByIds(Long[] maintenanceIds) {
        for (Long maintenanceId : maintenanceIds) {
            limsAuditTrailService.recordFieldChanges("lims_instrument_maintenance", maintenanceId,
                    "DELETE", null, null, "删除仪器维护");
        }
        return limsInstrumentMaintenanceMapper.deleteLimsInstrumentMaintenanceByIds(maintenanceIds);
    }
}
