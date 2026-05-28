package com.wei.lims.test.mapper;

import java.util.List;
import com.wei.lims.test.domain.LimsTestTemplate;

public interface LimsTestTemplateMapper {

    public LimsTestTemplate selectLimsTestTemplateByTemplateId(Long templateId);

    public LimsTestTemplate selectLimsTestTemplateByTemplateCode(String templateCode);

    public List<LimsTestTemplate> selectLimsTestTemplateList(LimsTestTemplate template);

    public int insertLimsTestTemplate(LimsTestTemplate template);

    public int updateLimsTestTemplate(LimsTestTemplate template);

    public int deleteLimsTestTemplateByTemplateIds(Long[] templateIds);
}
