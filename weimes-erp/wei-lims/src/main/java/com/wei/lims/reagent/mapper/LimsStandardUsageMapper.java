package com.wei.lims.reagent.mapper;

import java.util.List;
import com.wei.lims.reagent.domain.LimsStandardUsage;

public interface LimsStandardUsageMapper {

    public LimsStandardUsage selectByUsageId(Long usageId);

    public List<LimsStandardUsage> selectByStandardId(Long standardId);

    public List<LimsStandardUsage> selectLimsStandardUsageList(LimsStandardUsage usage);

    public int insertLimsStandardUsage(LimsStandardUsage usage);

    public int updateLimsStandardUsage(LimsStandardUsage usage);

    public int deleteLimsStandardUsageByIds(Long[] usageIds);
}
