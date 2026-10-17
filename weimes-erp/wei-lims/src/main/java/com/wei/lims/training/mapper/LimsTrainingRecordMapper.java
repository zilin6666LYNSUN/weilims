package com.wei.lims.training.mapper;

import java.util.List;
import com.wei.lims.training.domain.LimsTrainingRecord;

public interface LimsTrainingRecordMapper {
    public LimsTrainingRecord selectLimsTrainingRecordByTrainingId(Long trainingId);
    public List<LimsTrainingRecord> selectLimsTrainingRecordList(LimsTrainingRecord LimsTrainingRecord);
    public int insertLimsTrainingRecord(LimsTrainingRecord limstrainingrecord);
    public int updateLimsTrainingRecord(LimsTrainingRecord limstrainingrecord);
    public int deleteLimsTrainingRecordByTrainingId(Long[] trainingIds);
}