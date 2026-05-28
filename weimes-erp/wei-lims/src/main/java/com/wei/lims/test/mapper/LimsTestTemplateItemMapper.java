package com.wei.lims.test.mapper;

import java.util.List;
import com.wei.lims.test.domain.LimsTestTemplateItem;

public interface LimsTestTemplateItemMapper {

    public List<LimsTestTemplateItem> selectByTemplateId(Long templateId);

    public LimsTestTemplateItem selectById(Long id);

    public List<LimsTestTemplateItem> selectLimsTestTemplateItemList(LimsTestTemplateItem item);

    public int insertLimsTestTemplateItem(LimsTestTemplateItem item);

    public int updateLimsTestTemplateItem(LimsTestTemplateItem item);

    public int deleteLimsTestTemplateItemByIds(Long[] ids);

    public int deleteByTemplateId(Long templateId);
}
