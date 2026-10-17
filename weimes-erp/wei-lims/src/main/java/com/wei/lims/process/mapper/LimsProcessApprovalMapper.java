package com.wei.lims.process.mapper;

import java.util.List;
import com.wei.lims.process.domain.LimsProcessApproval;

/**
 * 流程审批记录Mapper接口
 */
public interface LimsProcessApprovalMapper {

    public LimsProcessApproval selectLimsProcessApprovalByApprovalId(Long approvalId);

    public List<LimsProcessApproval> selectLimsProcessApprovalList(LimsProcessApproval limsProcessApproval);

    public List<LimsProcessApproval> selectLimsProcessApprovalListByProcInstId(String procInstId);

    public List<LimsProcessApproval> selectLimsProcessApprovalListByBusiness(String businessType, Long businessId);

    public int insertLimsProcessApproval(LimsProcessApproval limsProcessApproval);
}
