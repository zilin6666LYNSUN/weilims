package com.wei.lims.oos.service;

import java.util.List;
import com.wei.lims.oos.domain.LimsOosPhaseRecord;

public interface ILimsOosPhaseRecordService {
    public List<LimsOosPhaseRecord> selectByInvestigationId(Long investigationId);
    public List<LimsOosPhaseRecord> selectLimsOosPhaseRecordList(LimsOosPhaseRecord record);
    public int insertLimsOosPhaseRecord(LimsOosPhaseRecord record);
    public int updateLimsOosPhaseRecord(LimsOosPhaseRecord record);
}
