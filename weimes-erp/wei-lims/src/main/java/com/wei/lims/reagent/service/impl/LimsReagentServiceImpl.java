package com.wei.lims.reagent.service.impl;

import java.util.List;
import com.wei.common.utils.DateUtils;
import com.wei.lims.reagent.domain.LimsReagent;
import com.wei.lims.reagent.mapper.LimsReagentMapper;
import com.wei.lims.reagent.service.ILimsReagentService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsReagentServiceImpl implements ILimsReagentService {

    @Autowired
    private LimsReagentMapper limsReagentMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsReagent selectByReagentId(Long reagentId) {
        return limsReagentMapper.selectByReagentId(reagentId);
    }

    @Override
    public List<LimsReagent> selectLimsReagentList(LimsReagent reagent) {
        return limsReagentMapper.selectLimsReagentList(reagent);
    }

    @Override
    @Transactional
    public int insertLimsReagent(LimsReagent reagent) {
        reagent.setCreateTime(DateUtils.getNowDate());
        int rows = limsReagentMapper.insertLimsReagent(reagent);
        limsAuditTrailService.recordFieldChanges("lims_reagent", reagent.getReagentId(),
                "INSERT", null, reagent, "新建试剂");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsReagent(LimsReagent reagent) {
        LimsReagent oldReagent = limsReagentMapper.selectByReagentId(reagent.getReagentId());
        reagent.setUpdateTime(DateUtils.getNowDate());
        int rows = limsReagentMapper.updateLimsReagent(reagent);
        limsAuditTrailService.recordFieldChanges("lims_reagent", reagent.getReagentId(),
                "UPDATE", oldReagent, reagent, "更新试剂");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsReagentByIds(Long[] reagentIds) {
        for (Long reagentId : reagentIds) {
            limsAuditTrailService.recordFieldChanges("lims_reagent", reagentId,
                    "DELETE", null, null, "删除试剂");
        }
        return limsReagentMapper.deleteLimsReagentByIds(reagentIds);
    }
}
