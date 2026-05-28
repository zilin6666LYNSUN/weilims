package com.wei.lims.reagent.service.impl;

import java.util.List;
import com.wei.common.utils.DateUtils;
import com.wei.lims.reagent.domain.LimsReagent;
import com.wei.lims.reagent.mapper.LimsReagentMapper;
import com.wei.lims.reagent.service.ILimsReagentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LimsReagentServiceImpl implements ILimsReagentService {

    @Autowired
    private LimsReagentMapper limsReagentMapper;

    @Override
    public LimsReagent selectByReagentId(Long reagentId) {
        return limsReagentMapper.selectByReagentId(reagentId);
    }

    @Override
    public List<LimsReagent> selectLimsReagentList(LimsReagent reagent) {
        return limsReagentMapper.selectLimsReagentList(reagent);
    }

    @Override
    public int insertLimsReagent(LimsReagent reagent) {
        reagent.setCreateTime(DateUtils.getNowDate());
        return limsReagentMapper.insertLimsReagent(reagent);
    }

    @Override
    public int updateLimsReagent(LimsReagent reagent) {
        reagent.setUpdateTime(DateUtils.getNowDate());
        return limsReagentMapper.updateLimsReagent(reagent);
    }

    @Override
    public int deleteLimsReagentByIds(Long[] reagentIds) {
        return limsReagentMapper.deleteLimsReagentByIds(reagentIds);
    }
}
