package com.wei.lims.report.mapper;

import java.util.List;
import com.wei.lims.report.domain.LimsDataValidationRule;

/**
 * 数据校验规则Mapper接口
 */
public interface LimsDataValidationRuleMapper {

    public LimsDataValidationRule selectRuleByRuleId(Long ruleId);

    public LimsDataValidationRule selectRuleByRuleCode(String ruleCode);

    public List<LimsDataValidationRule> selectRuleList(LimsDataValidationRule rule);

    public List<LimsDataValidationRule> selectEnabledRules();

    public int insertRule(LimsDataValidationRule rule);

    public int updateRule(LimsDataValidationRule rule);

    public int deleteRuleByRuleIds(Long[] ruleIds);
}
