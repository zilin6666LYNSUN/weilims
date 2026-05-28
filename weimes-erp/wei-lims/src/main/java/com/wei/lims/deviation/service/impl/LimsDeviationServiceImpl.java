package com.wei.lims.deviation.service.impl;

import java.util.List;
import com.wei.common.utils.DateUtils;
import com.wei.lims.deviation.domain.LimsDeviation;
import com.wei.lims.deviation.mapper.LimsDeviationMapper;
import com.wei.lims.deviation.service.ILimsDeviationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LimsDeviationServiceImpl implements ILimsDeviationService {

    @Autowired
    private LimsDeviationMapper limsDeviationMapper;

    @Override
    public LimsDeviation selectByDeviationId(Long deviationId) {
        return limsDeviationMapper.selectByDeviationId(deviationId);
    }

    @Override
    public List<LimsDeviation> selectLimsDeviationList(LimsDeviation deviation) {
        return limsDeviationMapper.selectLimsDeviationList(deviation);
    }

    @Override
    public int insertLimsDeviation(LimsDeviation deviation) {
        deviation.setCreateTime(DateUtils.getNowDate());
        return limsDeviationMapper.insertLimsDeviation(deviation);
    }

    @Override
    public int updateLimsDeviation(LimsDeviation deviation) {
        deviation.setUpdateTime(DateUtils.getNowDate());
        return limsDeviationMapper.updateLimsDeviation(deviation);
    }

    @Override
    public int deleteLimsDeviationByIds(Long[] deviationIds) {
        return limsDeviationMapper.deleteLimsDeviationByIds(deviationIds);
    }

    @Override
    public int submitDeviation(Long deviationId) {
        LimsDeviation d = limsDeviationMapper.selectByDeviationId(deviationId);
        d.setStatus("SUBMITTED");
        return limsDeviationMapper.updateLimsDeviation(d);
    }

    @Override
    public int approveDeviation(Long deviationId) {
        LimsDeviation d = limsDeviationMapper.selectByDeviationId(deviationId);
        d.setStatus("APPROVED");
        return limsDeviationMapper.updateLimsDeviation(d);
    }

    @Override
    public int closeDeviation(Long deviationId) {
        LimsDeviation d = limsDeviationMapper.selectByDeviationId(deviationId);
        d.setStatus("CLOSED");
        d.setCloseDate(DateUtils.getNowDate());
        return limsDeviationMapper.updateLimsDeviation(d);
    }
}
