package com.wei.lims.oos.mapper;

import java.util.List;
import com.wei.lims.oos.domain.LimsOosPhaseRecord;

public interface LimsOosPhaseRecordMapper {
    public LimsOosPhaseRecord selectByPhaseRecordId(Long phaseRecordId);
    public List<LimsOosPhaseRecord> selectByInvestigationId(Long investigationId);
    public List<LimsOosPhaseRecord> selectLimsOosPhaseRecordList(LimsOosPhaseRecord record);
    public int insertLimsOosPhaseRecord(LimsOosPhaseRecord record);
    public int updateLimsOosPhaseRecord(LimsOosPhaseRecord record);
    public int deleteLimsOosPhaseRecordByIds(Long[] phaseRecordIds);
}
