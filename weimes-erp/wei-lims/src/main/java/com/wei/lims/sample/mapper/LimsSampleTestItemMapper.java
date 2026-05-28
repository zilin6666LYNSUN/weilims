package com.wei.lims.sample.mapper;

import java.util.List;
import com.wei.lims.sample.domain.LimsSampleTestItem;

/**
 * 样品-检测项关联Mapper接口
 */
public interface LimsSampleTestItemMapper {

    public List<LimsSampleTestItem> selectBySampleId(Long sampleId);

    public LimsSampleTestItem selectById(Long id);

    public List<LimsSampleTestItem> selectLimsSampleTestItemList(LimsSampleTestItem item);

    public int insertLimsSampleTestItem(LimsSampleTestItem item);

    public int batchInsert(List<LimsSampleTestItem> list);

    public int updateLimsSampleTestItem(LimsSampleTestItem item);

    public int deleteBySampleId(Long sampleId);

    public int deleteById(Long id);
}
