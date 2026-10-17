package com.wei.lims.test.service;

import java.util.List;
import com.wei.lims.test.domain.LimsMicroTestRecord;

public interface ILimsMicroTestRecordService {

    public LimsMicroTestRecord selectByRecordId(Long recordId);

    public List<LimsMicroTestRecord> selectByTestOrderId(Long testOrderId);

    public List<LimsMicroTestRecord> selectLimsMicroTestRecordList(LimsMicroTestRecord record);

    public int insertLimsMicroTestRecord(LimsMicroTestRecord record);

    public int updateLimsMicroTestRecord(LimsMicroTestRecord record);

    public int deleteLimsMicroTestRecordByRecordIds(Long[] recordIds);

    /** 录入检测结果并自动判定结论（限度/无菌/菌种鉴定） */
    public int recordResult(LimsMicroTestRecord record);
}
