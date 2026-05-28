package com.wei.lims.reagent.service.impl;

import java.util.List;
import com.wei.lims.reagent.domain.LimsReagentStockLog;
import com.wei.lims.reagent.mapper.LimsReagentStockLogMapper;
import com.wei.lims.reagent.service.ILimsReagentStockLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LimsReagentStockLogServiceImpl implements ILimsReagentStockLogService {

    @Autowired
    private LimsReagentStockLogMapper limsReagentStockLogMapper;

    @Override
    public List<LimsReagentStockLog> selectByReagentId(Long reagentId) {
        return limsReagentStockLogMapper.selectByReagentId(reagentId);
    }

    @Override
    public List<LimsReagentStockLog> selectLimsReagentStockLogList(LimsReagentStockLog stockLog) {
        return limsReagentStockLogMapper.selectLimsReagentStockLogList(stockLog);
    }

    @Override
    public int insertLimsReagentStockLog(LimsReagentStockLog stockLog) {
        return limsReagentStockLogMapper.insertLimsReagentStockLog(stockLog);
    }
}
