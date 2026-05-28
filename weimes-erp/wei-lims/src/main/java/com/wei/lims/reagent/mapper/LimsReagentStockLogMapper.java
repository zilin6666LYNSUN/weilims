package com.wei.lims.reagent.mapper;

import java.util.List;
import com.wei.lims.reagent.domain.LimsReagentStockLog;

public interface LimsReagentStockLogMapper {
    public LimsReagentStockLog selectByStockLogId(Long stockLogId);
    public List<LimsReagentStockLog> selectByReagentId(Long reagentId);
    public List<LimsReagentStockLog> selectLimsReagentStockLogList(LimsReagentStockLog stockLog);
    public int insertLimsReagentStockLog(LimsReagentStockLog stockLog);
    public int deleteLimsReagentStockLogByIds(Long[] stockLogIds);
}
