package com.wei.lims.report.mapper;

import java.util.List;
import com.wei.lims.report.domain.LimsReportTemplate;

/**
 * 报告模板Mapper接口
 */
public interface LimsReportTemplateMapper {

    public LimsReportTemplate selectTemplateByTemplateId(Long templateId);

    public LimsReportTemplate selectTemplateByTemplateCode(String templateCode);

    public List<LimsReportTemplate> selectTemplateList(LimsReportTemplate template);

    public int insertTemplate(LimsReportTemplate template);

    public int updateTemplate(LimsReportTemplate template);

    public int deleteTemplateByTemplateIds(Long[] templateIds);

    public String checkTemplateCodeUnique(String templateCode);
}
