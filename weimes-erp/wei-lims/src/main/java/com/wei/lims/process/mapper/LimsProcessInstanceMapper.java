package com.wei.lims.process.mapper;

import java.util.List;
import com.wei.lims.process.domain.LimsProcessInstance;

/**
 * 流程实例Mapper接口
 */
public interface LimsProcessInstanceMapper {

    public LimsProcessInstance selectLimsProcessInstanceByProcInstId(String procInstId);

    public List<LimsProcessInstance> selectLimsProcessInstanceList(LimsProcessInstance limsProcessInstance);

    public List<LimsProcessInstance> selectRunningLimsProcessInstanceList(LimsProcessInstance limsProcessInstance);

    public int insertLimsProcessInstance(LimsProcessInstance limsProcessInstance);

    public int updateLimsProcessInstance(LimsProcessInstance limsProcessInstance);

    public int deleteLimsProcessInstanceByProcInstId(String procInstId);
}
