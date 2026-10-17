package com.wei.lims.test.service.impl;

import java.math.BigDecimal;
import java.util.List;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import com.wei.lims.test.domain.LimsMicroTestRecord;
import com.wei.lims.test.mapper.LimsMicroTestRecordMapper;
import com.wei.lims.test.service.ILimsMicroTestRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsMicroTestRecordServiceImpl implements ILimsMicroTestRecordService {

    @Autowired
    private LimsMicroTestRecordMapper limsMicroTestRecordMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsMicroTestRecord selectByRecordId(Long recordId) {
        return limsMicroTestRecordMapper.selectByRecordId(recordId);
    }

    @Override
    public List<LimsMicroTestRecord> selectByTestOrderId(Long testOrderId) {
        return limsMicroTestRecordMapper.selectByTestOrderId(testOrderId);
    }

    @Override
    public List<LimsMicroTestRecord> selectLimsMicroTestRecordList(LimsMicroTestRecord record) {
        return limsMicroTestRecordMapper.selectLimsMicroTestRecordList(record);
    }

    @Override
    @Transactional
    public int insertLimsMicroTestRecord(LimsMicroTestRecord record) {
        if (record.getStatus() == null) {
            record.setStatus("DRAFT");
        }
        int rows = limsMicroTestRecordMapper.insertLimsMicroTestRecord(record);
        limsAuditTrailService.recordFieldChanges("lims_micro_test_record", record.getRecordId(),
                "INSERT", null, record, "新建微生物检测记录");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsMicroTestRecord(LimsMicroTestRecord record) {
        LimsMicroTestRecord oldRecord = limsMicroTestRecordMapper.selectByRecordId(record.getRecordId());
        int rows = limsMicroTestRecordMapper.updateLimsMicroTestRecord(record);
        limsAuditTrailService.recordFieldChanges("lims_micro_test_record", record.getRecordId(),
                "UPDATE", oldRecord, record, "更新微生物检测记录");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsMicroTestRecordByRecordIds(Long[] recordIds) {
        for (Long recordId : recordIds) {
            LimsMicroTestRecord oldRecord = limsMicroTestRecordMapper.selectByRecordId(recordId);
            limsAuditTrailService.recordFieldChanges("lims_micro_test_record", recordId,
                    "DELETE", oldRecord, null, "删除微生物检测记录");
        }
        return limsMicroTestRecordMapper.deleteLimsMicroTestRecordByRecordIds(recordIds);
    }

    @Override
    @Transactional
    public int recordResult(LimsMicroTestRecord record) {
        LimsMicroTestRecord oldRecord = limsMicroTestRecordMapper.selectByRecordId(record.getRecordId());
        // 依据检测类型自动判定结论
        String conclusion = judge(record);
        record.setConclusion(conclusion);
        record.setStatus("COMPLETED");
        int rows = limsMicroTestRecordMapper.updateLimsMicroTestRecord(record);
        limsAuditTrailService.recordFieldChanges("lims_micro_test_record", record.getRecordId(),
                "UPDATE", oldRecord, record, "录入微生物检测结果，自动判定：" + conclusion);
        return rows;
    }

    /** 限度试验：菌落计数 <= 限度值判定合格；无菌试验：阳/阴性对照合规判定；菌种鉴定：result_value 视为符合度(0-100) */
    private String judge(LimsMicroTestRecord record) {
        if (record.getTestType() == null) {
            return "PENDING";
        }
        String type = record.getTestType().toUpperCase();
        if ("STERILITY".equals(type)) {
            if ("FAIL".equals(record.getPositiveControl()) || "FAIL".equals(record.getNegativeControl())) {
                return "FAIL";
            }
            return "PASS";
        }
        if ("IDENTIFICATION".equals(type)) {
            BigDecimal v = record.getResultValue();
            if (v != null && v.compareTo(new BigDecimal("95")) >= 0) {
                return "PASS";
            }
            return "FAIL";
        }
        // LIMIT 限度试验：colonyCount 与 resultValue(限度) 比较
        Integer count = record.getColonyCount();
        BigDecimal limit = record.getResultValue();
        if (count != null && limit != null) {
            return count.compareTo(limit.intValue()) <= 0 ? "PASS" : "FAIL";
        }
        if (limit == null) {
            return "PASS";
        }
        return "FAIL";
    }
}
