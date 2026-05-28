package com.wei.lims.test.mapper;

import java.util.List;
import com.wei.lims.test.domain.LimsMicroTestRecord;

public interface LimsMicroTestRecordMapper {

    public LimsMicroTestRecord selectByRecordId(Long recordId);

    public List<LimsMicroTestRecord> selectByTestOrderId(Long testOrderId);

    public List<LimsMicroTestRecord> selectLimsMicroTestRecordList(LimsMicroTestRecord record);

    public int insertLimsMicroTestRecord(LimsMicroTestRecord record);

    public int updateLimsMicroTestRecord(LimsMicroTestRecord record);

    public int deleteLimsMicroTestRecordByRecordIds(Long[] recordIds);
}
