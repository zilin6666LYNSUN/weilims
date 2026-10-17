package com.wei.lims.test.service.impl;

import java.util.List;
import com.wei.common.constant.UserConstants;
import com.wei.common.utils.DateUtils;
import com.wei.lims.test.domain.LimsTestTemplate;
import com.wei.lims.test.domain.LimsTestTemplateItem;
import com.wei.lims.test.mapper.LimsTestTemplateMapper;
import com.wei.lims.test.mapper.LimsTestTemplateItemMapper;
import com.wei.lims.test.service.ILimsTestTemplateService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsTestTemplateServiceImpl implements ILimsTestTemplateService {

    @Autowired
    private LimsTestTemplateMapper limsTestTemplateMapper;

    @Autowired
    private LimsTestTemplateItemMapper limsTestTemplateItemMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsTestTemplate selectLimsTestTemplateByTemplateId(Long templateId) {
        return limsTestTemplateMapper.selectLimsTestTemplateByTemplateId(templateId);
    }

    @Override
    public List<LimsTestTemplate> selectLimsTestTemplateList(LimsTestTemplate template) {
        return limsTestTemplateMapper.selectLimsTestTemplateList(template);
    }

    @Override
    @Transactional
    public int insertLimsTestTemplate(LimsTestTemplate template) {
        template.setCreateTime(DateUtils.getNowDate());
        int rows = limsTestTemplateMapper.insertLimsTestTemplate(template);
        limsAuditTrailService.recordFieldChanges("lims_test_template", template.getTemplateId(),
                "INSERT", null, template, "新建检验模板");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsTestTemplate(LimsTestTemplate template) {
        template.setUpdateTime(DateUtils.getNowDate());
        LimsTestTemplate oldTemplate = limsTestTemplateMapper.selectLimsTestTemplateByTemplateId(template.getTemplateId());
        int rows = limsTestTemplateMapper.updateLimsTestTemplate(template);
        limsAuditTrailService.recordFieldChanges("lims_test_template", template.getTemplateId(),
                "UPDATE", oldTemplate, template, "更新检验模板");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsTestTemplateByTemplateIds(Long[] templateIds) {
        for (Long templateId : templateIds) {
            LimsTestTemplate oldTemplate = limsTestTemplateMapper.selectLimsTestTemplateByTemplateId(templateId);
            limsAuditTrailService.recordFieldChanges("lims_test_template", templateId,
                    "DELETE", oldTemplate, null, "删除检验模板");
        }
        return limsTestTemplateMapper.deleteLimsTestTemplateByTemplateIds(templateIds);
    }

    @Override
    public String checkTemplateCodeUnique(LimsTestTemplate template) {
        LimsTestTemplate existing = limsTestTemplateMapper.selectLimsTestTemplateByTemplateCode(template.getTemplateCode());
        if (existing != null && !existing.getTemplateId().equals(template.getTemplateId())) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public List<LimsTestTemplateItem> selectItemsByTemplateId(Long templateId) {
        return limsTestTemplateItemMapper.selectByTemplateId(templateId);
    }

    @Override
    @Transactional
    public int insertTemplateItem(LimsTestTemplateItem item) {
        item.setCreateTime(DateUtils.getNowDate());
        int rows = limsTestTemplateItemMapper.insertLimsTestTemplateItem(item);
        limsAuditTrailService.recordFieldChanges("lims_test_template_item", item.getId(),
                "INSERT", null, item, "新建检验模板明细");
        return rows;
    }

    @Override
    @Transactional
    public int updateTemplateItem(LimsTestTemplateItem item) {
        item.setUpdateTime(DateUtils.getNowDate());
        LimsTestTemplateItem oldItem = limsTestTemplateItemMapper.selectById(item.getId());
        int rows = limsTestTemplateItemMapper.updateLimsTestTemplateItem(item);
        limsAuditTrailService.recordFieldChanges("lims_test_template_item", item.getId(),
                "UPDATE", oldItem, item, "更新检验模板明细");
        return rows;
    }

    @Override
    @Transactional
    public int deleteTemplateItemByIds(Long[] ids) {
        for (Long id : ids) {
            LimsTestTemplateItem oldItem = limsTestTemplateItemMapper.selectById(id);
            limsAuditTrailService.recordFieldChanges("lims_test_template_item", id,
                    "DELETE", oldItem, null, "删除检验模板明细");
        }
        return limsTestTemplateItemMapper.deleteLimsTestTemplateItemByIds(ids);
    }
}
