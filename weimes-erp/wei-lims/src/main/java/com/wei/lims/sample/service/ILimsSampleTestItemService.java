package com.wei.lims.sample.service;

import java.util.List;
import com.wei.lims.sample.domain.LimsSampleTestItem;

/**
 * 样品-检测项关联Service接口
 */
public interface ILimsSampleTestItemService {

    public List<LimsSampleTestItem> selectBySampleId(Long sampleId);

    public List<LimsSampleTestItem> selectLimsSampleTestItemList(LimsSampleTestItem item);

    public int insertLimsSampleTestItem(LimsSampleTestItem item);

    public int batchInsert(List<LimsSampleTestItem> list);

    public int updateLimsSampleTestItem(LimsSampleTestItem item);

    public int deleteBySampleId(Long sampleId);

    public int deleteById(Long id);
}
