package com.wei.lims.reagent.service.impl;

import java.util.List;
import com.wei.lims.reagent.domain.LimsReagentStockLog;
import com.wei.lims.reagent.mapper.LimsReagentStockLogMapper;
import com.wei.lims.reagent.service.ILimsReagentStockLogService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsReagentStockLogServiceImpl implements ILimsReagentStockLogService {

    @Autowired
    private LimsReagentStockLogMapper limsReagentStockLogMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public List<LimsReagentStockLog> selectByReagentId(Long reagentId) {
        return limsReagentStockLogMapper.selectByReagentId(reagentId);
    }

    @Override
    public List<LimsReagentStockLog> selectLimsReagentStockLogList(LimsReagentStockLog stockLog) {
        return limsReagentStockLogMapper.selectLimsReagentStockLogList(stockLog);
    }

    @Override
    @Transactional
    public int insertLimsReagentStockLog(LimsReagentStockLog stockLog) {
        int rows = limsReagentStockLogMapper.insertLimsReagentStockLog(stockLog);
        limsAuditTrailService.recordFieldChanges("lims_reagent_stock_log", stockLog.getStockLogId(),
                "INSERT", null, stockLog, "新建试剂库存流水");
        return rows;
    }
}
