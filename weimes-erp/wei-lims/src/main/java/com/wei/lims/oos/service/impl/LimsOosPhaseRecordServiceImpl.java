package com.wei.lims.oos.service.impl;

import java.util.List;
import com.wei.lims.oos.domain.LimsOosPhaseRecord;
import com.wei.lims.oos.mapper.LimsOosPhaseRecordMapper;
import com.wei.lims.oos.service.ILimsOosPhaseRecordService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsOosPhaseRecordServiceImpl implements ILimsOosPhaseRecordService {

    @Autowired
    private LimsOosPhaseRecordMapper limsOosPhaseRecordMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public List<LimsOosPhaseRecord> selectByInvestigationId(Long investigationId) {
        return limsOosPhaseRecordMapper.selectByInvestigationId(investigationId);
    }

    @Override
    public List<LimsOosPhaseRecord> selectLimsOosPhaseRecordList(LimsOosPhaseRecord record) {
        return limsOosPhaseRecordMapper.selectLimsOosPhaseRecordList(record);
    }

    @Override
    @Transactional
    public int insertLimsOosPhaseRecord(LimsOosPhaseRecord record) {
        int rows = limsOosPhaseRecordMapper.insertLimsOosPhaseRecord(record);
        limsAuditTrailService.recordFieldChanges("lims_oos_phase_record", record.getPhaseRecordId(),
                "INSERT", null, record, "新建OOS阶段记录");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsOosPhaseRecord(LimsOosPhaseRecord record) {
        LimsOosPhaseRecord oldRecord = limsOosPhaseRecordMapper.selectByPhaseRecordId(record.getPhaseRecordId());
        int rows = limsOosPhaseRecordMapper.updateLimsOosPhaseRecord(record);
        limsAuditTrailService.recordFieldChanges("lims_oos_phase_record", record.getPhaseRecordId(),
                "UPDATE", oldRecord, record, "更新OOS阶段记录");
        return rows;
    }
}
