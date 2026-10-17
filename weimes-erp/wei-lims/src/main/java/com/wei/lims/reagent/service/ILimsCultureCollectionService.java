package com.wei.lims.reagent.service;

import java.util.List;
import com.wei.lims.reagent.domain.LimsCultureCollection;

public interface ILimsCultureCollectionService {
    public LimsCultureCollection selectByCultureId(Long cultureId);
    public List<LimsCultureCollection> selectLimsCultureCollectionList(LimsCultureCollection culture);
    public int insertLimsCultureCollection(LimsCultureCollection culture);
    public int updateLimsCultureCollection(LimsCultureCollection culture);
    public int deleteLimsCultureCollectionByIds(Long[] cultureIds);

    /** 接收入库：状态流转为在用(ACTIVE) */
    public int receiveCulture(Long cultureId);

    /** 传代：传代次数+1，记录活力检测结果 */
    public int passageCulture(Long cultureId, String viabilityResult);

    /** 销毁出库：状态流转为已销毁(DISCARDED) */
    public int discardCulture(Long cultureId, String reason);
}
