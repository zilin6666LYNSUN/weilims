package com.wei.lims.report.service;

import java.util.List;
import com.wei.lims.report.domain.LimsDataValidationRule;

/**
 * 数据校验规则Service接口
 */
public interface ILimsDataValidationRuleService {

    public LimsDataValidationRule selectRuleByRuleId(Long ruleId);

    public List<LimsDataValidationRule> selectRuleList(LimsDataValidationRule rule);

    public List<LimsDataValidationRule> selectEnabledRules();

    public int insertRule(LimsDataValidationRule rule);

    public int updateRule(LimsDataValidationRule rule);

    public int deleteRuleByRuleIds(Long[] ruleIds);
}
