package com.wei.lims.entrust.mapper;

import java.util.List;
import com.wei.lims.entrust.domain.LimsEntrustTemplate;

public interface LimsEntrustTemplateMapper {
    public LimsEntrustTemplate selectLimsEntrustTemplateByTemplateId(Long templateId);
    public List<LimsEntrustTemplate> selectLimsEntrustTemplateList(LimsEntrustTemplate LimsEntrustTemplate);
    public int insertLimsEntrustTemplate(LimsEntrustTemplate limsentrusttemplate);
    public int updateLimsEntrustTemplate(LimsEntrustTemplate limsentrusttemplate);
    public int deleteLimsEntrustTemplateByTemplateId(Long[] templateIds);
}