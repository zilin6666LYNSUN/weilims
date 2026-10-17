package com.wei.lims.entrust.service.impl;

import java.util.List;
import com.wei.lims.entrust.domain.LimsEntrustTemplate;
import com.wei.lims.entrust.mapper.LimsEntrustTemplateMapper;
import com.wei.lims.entrust.service.ILimsEntrustTemplateService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsEntrustTemplateServiceImpl implements ILimsEntrustTemplateService {

    @Autowired
    private LimsEntrustTemplateMapper limsentrusttemplateMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsEntrustTemplate selectLimsEntrustTemplateByTemplateId(Long templateId) {
        return limsentrusttemplateMapper.selectLimsEntrustTemplateByTemplateId(templateId);
    }

    @Override
    public List<LimsEntrustTemplate> selectLimsEntrustTemplateList(LimsEntrustTemplate limsentrusttemplate) {
        return limsentrusttemplateMapper.selectLimsEntrustTemplateList(limsentrusttemplate);
    }

    @Override
    @Transactional
    public int insertLimsEntrustTemplate(LimsEntrustTemplate limsentrusttemplate) {
        int rows = limsentrusttemplateMapper.insertLimsEntrustTemplate(limsentrusttemplate);
        limsAuditTrailService.recordFieldChanges("lims_entrust_template", limsentrusttemplate.getTemplateId(), "INSERT", null, limsentrusttemplate, "新建委托单模板");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsEntrustTemplate(LimsEntrustTemplate limsentrusttemplate) {
        LimsEntrustTemplate old = limsentrusttemplateMapper.selectLimsEntrustTemplateByTemplateId(limsentrusttemplate.getTemplateId());
        int rows = limsentrusttemplateMapper.updateLimsEntrustTemplate(limsentrusttemplate);
        limsAuditTrailService.recordFieldChanges("lims_entrust_template", limsentrusttemplate.getTemplateId(), "UPDATE", old, limsentrusttemplate, "更新委托单模板");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsEntrustTemplateByTemplateId(Long[] templateIds) {
        for (Long templateId : templateIds) {
            LimsEntrustTemplate old = limsentrusttemplateMapper.selectLimsEntrustTemplateByTemplateId(templateId);
            limsAuditTrailService.recordFieldChanges("lims_entrust_template", templateId, "DELETE", old, null, "删除委托单模板");
        }
        return limsentrusttemplateMapper.deleteLimsEntrustTemplateByTemplateId(templateIds);
    }
}