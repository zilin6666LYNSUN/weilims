package com.wei.lims.report.service;

import java.util.List;
import com.wei.lims.report.domain.LimsReportTemplate;

/**
 * 报告模板Service接口
 */
public interface ILimsReportTemplateService {

    public LimsReportTemplate selectTemplateByTemplateId(Long templateId);

    public List<LimsReportTemplate> selectTemplateList(LimsReportTemplate template);

    public int insertTemplate(LimsReportTemplate template);

    public int updateTemplate(LimsReportTemplate template);

    public int deleteTemplateByTemplateIds(Long[] templateIds);

    public String checkTemplateCodeUnique(String templateCode);
}
