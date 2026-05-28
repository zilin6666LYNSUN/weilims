package com.wei.lims.test.service;

import java.util.List;
import com.wei.lims.test.domain.LimsTestTemplate;
import com.wei.lims.test.domain.LimsTestTemplateItem;

/**
 * 检验模板Service接口
 */
public interface ILimsTestTemplateService {

    public LimsTestTemplate selectLimsTestTemplateByTemplateId(Long templateId);

    public List<LimsTestTemplate> selectLimsTestTemplateList(LimsTestTemplate template);

    public int insertLimsTestTemplate(LimsTestTemplate template);

    public int updateLimsTestTemplate(LimsTestTemplate template);

    public int deleteLimsTestTemplateByTemplateIds(Long[] templateIds);

    public String checkTemplateCodeUnique(LimsTestTemplate template);

    /** 模板检测项操作 */
    public List<LimsTestTemplateItem> selectItemsByTemplateId(Long templateId);

    public int insertTemplateItem(LimsTestTemplateItem item);

    public int updateTemplateItem(LimsTestTemplateItem item);

    public int deleteTemplateItemByIds(Long[] ids);
}
