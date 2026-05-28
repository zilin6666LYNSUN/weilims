package com.wei.lims.test.service.impl;

import java.util.List;
import com.wei.common.constant.UserConstants;
import com.wei.common.utils.DateUtils;
import com.wei.lims.test.domain.LimsTestTemplate;
import com.wei.lims.test.domain.LimsTestTemplateItem;
import com.wei.lims.test.mapper.LimsTestTemplateMapper;
import com.wei.lims.test.mapper.LimsTestTemplateItemMapper;
import com.wei.lims.test.service.ILimsTestTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsTestTemplateServiceImpl implements ILimsTestTemplateService {

    @Autowired
    private LimsTestTemplateMapper limsTestTemplateMapper;

    @Autowired
    private LimsTestTemplateItemMapper limsTestTemplateItemMapper;

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
        return limsTestTemplateMapper.insertLimsTestTemplate(template);
    }

    @Override
    @Transactional
    public int updateLimsTestTemplate(LimsTestTemplate template) {
        template.setUpdateTime(DateUtils.getNowDate());
        return limsTestTemplateMapper.updateLimsTestTemplate(template);
    }

    @Override
    public int deleteLimsTestTemplateByTemplateIds(Long[] templateIds) {
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
    public int insertTemplateItem(LimsTestTemplateItem item) {
        item.setCreateTime(DateUtils.getNowDate());
        return limsTestTemplateItemMapper.insertLimsTestTemplateItem(item);
    }

    @Override
    public int updateTemplateItem(LimsTestTemplateItem item) {
        item.setUpdateTime(DateUtils.getNowDate());
        return limsTestTemplateItemMapper.updateLimsTestTemplateItem(item);
    }

    @Override
    public int deleteTemplateItemByIds(Long[] ids) {
        return limsTestTemplateItemMapper.deleteLimsTestTemplateItemByIds(ids);
    }
}
