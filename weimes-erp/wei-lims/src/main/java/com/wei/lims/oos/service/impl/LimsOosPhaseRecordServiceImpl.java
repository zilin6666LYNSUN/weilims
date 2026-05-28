package com.wei.lims.oos.service.impl;

import java.util.List;
import com.wei.lims.oos.domain.LimsOosPhaseRecord;
import com.wei.lims.oos.mapper.LimsOosPhaseRecordMapper;
import com.wei.lims.oos.service.ILimsOosPhaseRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LimsOosPhaseRecordServiceImpl implements ILimsOosPhaseRecordService {

    @Autowired
    private LimsOosPhaseRecordMapper limsOosPhaseRecordMapper;

    @Override
    public List<LimsOosPhaseRecord> selectByInvestigationId(Long investigationId) {
        return limsOosPhaseRecordMapper.selectByInvestigationId(investigationId);
    }

    @Override
    public List<LimsOosPhaseRecord> selectLimsOosPhaseRecordList(LimsOosPhaseRecord record) {
        return limsOosPhaseRecordMapper.selectLimsOosPhaseRecordList(record);
    }

    @Override
    public int insertLimsOosPhaseRecord(LimsOosPhaseRecord record) {
        return limsOosPhaseRecordMapper.insertLimsOosPhaseRecord(record);
    }

    @Override
    public int updateLimsOosPhaseRecord(LimsOosPhaseRecord record) {
        return limsOosPhaseRecordMapper.updateLimsOosPhaseRecord(record);
    }
}
