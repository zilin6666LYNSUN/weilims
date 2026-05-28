package com.wei.lims.reagent.service;

import java.util.List;
import com.wei.lims.reagent.domain.LimsReagentStockLog;

public interface ILimsReagentStockLogService {
    public List<LimsReagentStockLog> selectByReagentId(Long reagentId);
    public List<LimsReagentStockLog> selectLimsReagentStockLogList(LimsReagentStockLog stockLog);
    public int insertLimsReagentStockLog(LimsReagentStockLog stockLog);
}
