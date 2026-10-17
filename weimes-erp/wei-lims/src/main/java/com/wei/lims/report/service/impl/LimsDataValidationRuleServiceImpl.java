package com.wei.lims.report.service.impl;

import java.util.List;
import com.wei.lims.report.domain.LimsDataValidationRule;
import com.wei.lims.report.mapper.LimsDataValidationRuleMapper;
import com.wei.lims.report.service.ILimsDataValidationRuleService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsDataValidationRuleServiceImpl implements ILimsDataValidationRuleService {

    @Autowired
    private LimsDataValidationRuleMapper limsDataValidationRuleMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

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
    @Transactional
    public int insertRule(LimsDataValidationRule rule) {
        rule.setCreateTime(new java.util.Date());
        int rows = limsDataValidationRuleMapper.insertRule(rule);
        limsAuditTrailService.recordFieldChanges("lims_data_validation_rule", rule.getRuleId(),
                "INSERT", null, rule, "新建数据校验规则");
        return rows;
    }

    @Override
    @Transactional
    public int updateRule(LimsDataValidationRule rule) {
        rule.setUpdateTime(new java.util.Date());
        LimsDataValidationRule oldRule = limsDataValidationRuleMapper.selectRuleByRuleId(rule.getRuleId());
        int rows = limsDataValidationRuleMapper.updateRule(rule);
        limsAuditTrailService.recordFieldChanges("lims_data_validation_rule", rule.getRuleId(),
                "UPDATE", oldRule, rule, "更新数据校验规则");
        return rows;
    }

    @Override
    @Transactional
    public int deleteRuleByRuleIds(Long[] ruleIds) {
        for (Long ruleId : ruleIds) {
            LimsDataValidationRule oldRule = limsDataValidationRuleMapper.selectRuleByRuleId(ruleId);
            limsAuditTrailService.recordFieldChanges("lims_data_validation_rule", ruleId,
                    "DELETE", oldRule, null, "删除数据校验规则");
        }
        return limsDataValidationRuleMapper.deleteRuleByRuleIds(ruleIds);
    }
}
