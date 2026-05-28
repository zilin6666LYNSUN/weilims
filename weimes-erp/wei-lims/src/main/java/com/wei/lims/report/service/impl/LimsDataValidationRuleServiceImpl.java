package com.wei.lims.report.service.impl;

import java.util.List;
import com.wei.lims.report.domain.LimsDataValidationRule;
import com.wei.lims.report.mapper.LimsDataValidationRuleMapper;
import com.wei.lims.report.service.ILimsDataValidationRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LimsDataValidationRuleServiceImpl implements ILimsDataValidationRuleService {

    @Autowired
    private LimsDataValidationRuleMapper limsDataValidationRuleMapper;

    @Override
    public LimsDataValidationRule selectRuleByRuleId(Long ruleId) {
        return limsDataValidationRuleMapper.selectRuleByRuleId(ruleId);
    }

    @Override
    public List<LimsDataValidationRule> selectRuleList(LimsDataValidationRule rule) {
        return limsDataValidationRuleMapper.selectRuleList(rule);
    }

    @Override
    public List<LimsDataValidationRule> selectEnabledRules() {
        return limsDataValidationRuleMapper.selectEnabledRules();
    }

    @Override
    public int insertRule(LimsDataValidationRule rule) {
        rule.setCreateTime(new java.util.Date());
        return limsDataValidationRuleMapper.insertRule(rule);
    }

    @Override
    public int updateRule(LimsDataValidationRule rule) {
        rule.setUpdateTime(new java.util.Date());
        return limsDataValidationRuleMapper.updateRule(rule);
    }

    @Override
    public int deleteRuleByRuleIds(Long[] ruleIds) {
        return limsDataValidationRuleMapper.deleteRuleByRuleIds(ruleIds);
    }
}
