package com.wei.lims.report.service.impl;

import java.util.List;
import com.wei.common.utils.DateUtils;
import com.wei.lims.report.domain.LimsReportTemplate;
import com.wei.lims.report.mapper.LimsReportTemplateMapper;
import com.wei.lims.report.service.ILimsReportTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LimsReportTemplateServiceImpl implements ILimsReportTemplateService {

    @Autowired
    private LimsReportTemplateMapper limsReportTemplateMapper;

    @Override
    public LimsReportTemplate selectTemplateByTemplateId(Long templateId) {
        return limsReportTemplateMapper.selectTemplateByTemplateId(templateId);
    }

    @Override
    public List<LimsReportTemplate> selectTemplateList(LimsReportTemplate template) {
        return limsReportTemplateMapper.selectTemplateList(template);
    }

    @Override
    public int insertTemplate(LimsReportTemplate template) {
        template.setCreateTime(DateUtils.getNowDate());
        return limsReportTemplateMapper.insertTemplate(template);
    }

    @Override
    public int updateTemplate(LimsReportTemplate template) {
        template.setUpdateTime(DateUtils.getNowDate());
        return limsReportTemplateMapper.updateTemplate(template);
    }

    @Override
    public int deleteTemplateByTemplateIds(Long[] templateIds) {
        return limsReportTemplateMapper.deleteTemplateByTemplateIds(templateIds);
    }

    @Override
    public String checkTemplateCodeUnique(String templateCode) {
        LimsReportTemplate existing = limsReportTemplateMapper.selectTemplateByTemplateCode(templateCode);
        return existing != null ? "1" : "0";
    }
}
