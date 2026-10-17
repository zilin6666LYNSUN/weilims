package com.wei.lims.training.service.impl;

import java.util.List;
import com.wei.lims.training.domain.LimsTrainingRecord;
import com.wei.lims.training.mapper.LimsTrainingRecordMapper;
import com.wei.lims.training.service.ILimsTrainingRecordService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsTrainingRecordServiceImpl implements ILimsTrainingRecordService {

    @Autowired
    private LimsTrainingRecordMapper limstrainingrecordMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsTrainingRecord selectLimsTrainingRecordByTrainingId(Long trainingId) {
        return limstrainingrecordMapper.selectLimsTrainingRecordByTrainingId(trainingId);
    }

    @Override
    public List<LimsTrainingRecord> selectLimsTrainingRecordList(LimsTrainingRecord limstrainingrecord) {
        return limstrainingrecordMapper.selectLimsTrainingRecordList(limstrainingrecord);
    }

    @Override
    @Transactional
    public int insertLimsTrainingRecord(LimsTrainingRecord limstrainingrecord) {
        int rows = limstrainingrecordMapper.insertLimsTrainingRecord(limstrainingrecord);
        limsAuditTrailService.recordFieldChanges("lims_training_record", limstrainingrecord.getTrainingId(), "INSERT", null, limstrainingrecord, "新建培训记录");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsTrainingRecord(LimsTrainingRecord limstrainingrecord) {
        LimsTrainingRecord old = limstrainingrecordMapper.selectLimsTrainingRecordByTrainingId(limstrainingrecord.getTrainingId());
        int rows = limstrainingrecordMapper.updateLimsTrainingRecord(limstrainingrecord);
        limsAuditTrailService.recordFieldChanges("lims_training_record", limstrainingrecord.getTrainingId(), "UPDATE", old, limstrainingrecord, "更新培训记录");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsTrainingRecordByTrainingId(Long[] trainingIds) {
        for (Long trainingId : trainingIds) {
            LimsTrainingRecord old = limstrainingrecordMapper.selectLimsTrainingRecordByTrainingId(trainingId);
            limsAuditTrailService.recordFieldChanges("lims_training_record", trainingId, "DELETE", old, null, "删除培训记录");
        }
        return limstrainingrecordMapper.deleteLimsTrainingRecordByTrainingId(trainingIds);
    }
}