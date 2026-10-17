package com.wei.lims.report.service.impl;

import java.util.List;
import com.wei.common.utils.DateUtils;
import com.wei.lims.report.domain.LimsReportTemplate;
import com.wei.lims.report.mapper.LimsReportTemplateMapper;
import com.wei.lims.report.service.ILimsReportTemplateService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsReportTemplateServiceImpl implements ILimsReportTemplateService {

    @Autowired
    private LimsReportTemplateMapper limsReportTemplateMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsReportTemplate selectTemplateByTemplateId(Long templateId) {
        return limsReportTemplateMapper.selectTemplateByTemplateId(templateId);
    }

    @Override
    public List<LimsReportTemplate> selectTemplateList(LimsReportTemplate template) {
        return limsReportTemplateMapper.selectTemplateList(template);
    }

    @Override
    @Transactional
    public int insertTemplate(LimsReportTemplate template) {
        template.setCreateTime(DateUtils.getNowDate());
        int rows = limsReportTemplateMapper.insertTemplate(template);
        limsAuditTrailService.recordFieldChanges("lims_report_template", template.getTemplateId(),
                "INSERT", null, template, "新建报告模板");
        return rows;
    }

    @Override
    @Transactional
    public int updateTemplate(LimsReportTemplate template) {
        template.setUpdateTime(DateUtils.getNowDate());
        LimsReportTemplate oldTemplate = limsReportTemplateMapper.selectTemplateByTemplateId(template.getTemplateId());
        int rows = limsReportTemplateMapper.updateTemplate(template);
        limsAuditTrailService.recordFieldChanges("lims_report_template", template.getTemplateId(),
                "UPDATE", oldTemplate, template, "更新报告模板");
        return rows;
    }

    @Override
    @Transactional
    public int deleteTemplateByTemplateIds(Long[] templateIds) {
        for (Long templateId : templateIds) {
            LimsReportTemplate oldTemplate = limsReportTemplateMapper.selectTemplateByTemplateId(templateId);
            limsAuditTrailService.recordFieldChanges("lims_report_template", templateId,
                    "DELETE", oldTemplate, null, "删除报告模板");
        }
        return limsReportTemplateMapper.deleteTemplateByTemplateIds(templateIds);
    }

    @Override
    public String checkTemplateCodeUnique(String templateCode) {
        LimsReportTemplate existing = limsReportTemplateMapper.selectTemplateByTemplateCode(templateCode);
        return existing != null ? "1" : "0";
    }
}
