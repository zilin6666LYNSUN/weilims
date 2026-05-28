package com.wei.lims.sample.service.impl;

import java.util.List;
import com.wei.lims.sample.domain.LimsSampleTestItem;
import com.wei.lims.sample.mapper.LimsSampleTestItemMapper;
import com.wei.lims.sample.service.ILimsSampleTestItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LimsSampleTestItemServiceImpl implements ILimsSampleTestItemService {

    @Autowired
    private LimsSampleTestItemMapper limsSampleTestItemMapper;

    @Override
    public List<LimsSampleTestItem> selectBySampleId(Long sampleId) {
        return limsSampleTestItemMapper.selectBySampleId(sampleId);
    }

    @Override
    public List<LimsSampleTestItem> selectLimsSampleTestItemList(LimsSampleTestItem item) {
        return limsSampleTestItemMapper.selectLimsSampleTestItemList(item);
    }

    @Override
    public int insertLimsSampleTestItem(LimsSampleTestItem item) {
        return limsSampleTestItemMapper.insertLimsSampleTestItem(item);
    }

    @Override
    public int batchInsert(List<LimsSampleTestItem> list) {
        return limsSampleTestItemMapper.batchInsert(list);
    }

    @Override
    public int updateLimsSampleTestItem(LimsSampleTestItem item) {
        return limsSampleTestItemMapper.updateLimsSampleTestItem(item);
    }

    @Override
    public int deleteBySampleId(Long sampleId) {
        return limsSampleTestItemMapper.deleteBySampleId(sampleId);
    }

    @Override
    public int deleteById(Long id) {
        return limsSampleTestItemMapper.deleteById(id);
    }
}
