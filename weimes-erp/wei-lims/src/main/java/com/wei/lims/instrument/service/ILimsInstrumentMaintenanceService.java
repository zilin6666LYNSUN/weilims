package com.wei.lims.instrument.service;

import java.util.List;
import com.wei.lims.instrument.domain.LimsInstrumentMaintenance;

public interface ILimsInstrumentMaintenanceService {
    public LimsInstrumentMaintenance selectByMaintenanceId(Long maintenanceId);
    public List<LimsInstrumentMaintenance> selectByInstrumentId(Long instrumentId);
    public List<LimsInstrumentMaintenance> selectLimsInstrumentMaintenanceList(LimsInstrumentMaintenance maintenance);
    public int insertLimsInstrumentMaintenance(LimsInstrumentMaintenance maintenance);
    public int updateLimsInstrumentMaintenance(LimsInstrumentMaintenance maintenance);
    public int deleteLimsInstrumentMaintenanceByIds(Long[] maintenanceIds);
}
