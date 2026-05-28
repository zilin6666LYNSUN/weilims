package com.wei.lims.reagent.mapper;

import java.util.List;
import com.wei.lims.reagent.domain.LimsCultureCollection;

public interface LimsCultureCollectionMapper {
    public LimsCultureCollection selectByCultureId(Long cultureId);
    public List<LimsCultureCollection> selectLimsCultureCollectionList(LimsCultureCollection culture);
    public int insertLimsCultureCollection(LimsCultureCollection culture);
    public int updateLimsCultureCollection(LimsCultureCollection culture);
    public int deleteLimsCultureCollectionByIds(Long[] cultureIds);
}
