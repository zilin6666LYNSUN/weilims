package com.wei.lims.reagent.service.impl;

import java.util.List;
import com.wei.lims.reagent.domain.LimsCultureCollection;
import com.wei.lims.reagent.mapper.LimsCultureCollectionMapper;
import com.wei.lims.reagent.service.ILimsCultureCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LimsCultureCollectionServiceImpl implements ILimsCultureCollectionService {

    @Autowired
    private LimsCultureCollectionMapper limsCultureCollectionMapper;

    @Override
    public LimsCultureCollection selectByCultureId(Long cultureId) {
        return limsCultureCollectionMapper.selectByCultureId(cultureId);
    }

    @Override
    public List<LimsCultureCollection> selectLimsCultureCollectionList(LimsCultureCollection culture) {
        return limsCultureCollectionMapper.selectLimsCultureCollectionList(culture);
    }

    @Override
    public int insertLimsCultureCollection(LimsCultureCollection culture) {
        return limsCultureCollectionMapper.insertLimsCultureCollection(culture);
    }

    @Override
    public int updateLimsCultureCollection(LimsCultureCollection culture) {
        return limsCultureCollectionMapper.updateLimsCultureCollection(culture);
    }

    @Override
    public int deleteLimsCultureCollectionByIds(Long[] cultureIds) {
        return limsCultureCollectionMapper.deleteLimsCultureCollectionByIds(cultureIds);
    }
}
