package com.wei.lims.process.mapper;

import java.util.List;
import com.wei.lims.process.domain.LimsProcessDefinition;

/**
 * 流程定义Mapper接口
 */
public interface LimsProcessDefinitionMapper {

    public LimsProcessDefinition selectLimsProcessDefinitionByProcDefId(Long procDefId);

    public LimsProcessDefinition selectLimsProcessDefinitionByProcKey(String procKey);

    public LimsProcessDefinition selectActiveLimsProcessDefinitionByProcKey(String procKey);

    public List<LimsProcessDefinition> selectLimsProcessDefinitionList(LimsProcessDefinition limsProcessDefinition);

    public int insertLimsProcessDefinition(LimsProcessDefinition limsProcessDefinition);

    public int updateLimsProcessDefinition(LimsProcessDefinition limsProcessDefinition);

    public int deleteLimsProcessDefinitionByProcDefIds(Long[] procDefIds);

    /** 按角色编码查询角色下有效用户ID列表(审批人=ROLE解析用) */
    public List<Long> selectUserIdsByRoleKey(String roleKey);
}
