package com.wei.lims.change.service.impl;

import java.util.List;
import com.wei.common.utils.DateUtils;
import com.wei.lims.change.domain.LimsChangeControl;
import com.wei.lims.change.mapper.LimsChangeControlMapper;
import com.wei.lims.change.service.ILimsChangeControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LimsChangeControlServiceImpl implements ILimsChangeControlService {

    @Autowired
    private LimsChangeControlMapper limsChangeControlMapper;

    @Override
    public LimsChangeControl selectByChangeId(Long changeId) {
        return limsChangeControlMapper.selectByChangeId(changeId);
    }

    @Override
    public List<LimsChangeControl> selectLimsChangeControlList(LimsChangeControl changeControl) {
        return limsChangeControlMapper.selectLimsChangeControlList(changeControl);
    }

    @Override
    public int insertLimsChangeControl(LimsChangeControl changeControl) {
        changeControl.setCreateTime(DateUtils.getNowDate());
        return limsChangeControlMapper.insertLimsChangeControl(changeControl);
    }

    @Override
    public int updateLimsChangeControl(LimsChangeControl changeControl) {
        changeControl.setUpdateTime(DateUtils.getNowDate());
        return limsChangeControlMapper.updateLimsChangeControl(changeControl);
    }

    @Override
    public int deleteLimsChangeControlByIds(Long[] changeIds) {
        return limsChangeControlMapper.deleteLimsChangeControlByIds(changeIds);
    }

    @Override
    public int submitChange(Long changeId) {
        LimsChangeControl c = limsChangeControlMapper.selectByChangeId(changeId);
        c.setStatus("SUBMITTED");
        return limsChangeControlMapper.updateLimsChangeControl(c);
    }

    @Override
    public int approveChange(Long changeId) {
        LimsChangeControl c = limsChangeControlMapper.selectByChangeId(changeId);
        c.setStatus("APPROVED");
        return limsChangeControlMapper.updateLimsChangeControl(c);
    }

    @Override
    public int implementChange(Long changeId) {
        LimsChangeControl c = limsChangeControlMapper.selectByChangeId(changeId);
        c.setStatus("IMPLEMENTING");
        c.setEffectiveDate(DateUtils.getNowDate());
        return limsChangeControlMapper.updateLimsChangeControl(c);
    }

    @Override
    public int completeChange(Long changeId) {
        LimsChangeControl c = limsChangeControlMapper.selectByChangeId(changeId);
        c.setStatus("COMPLETED");
        c.setEffectiveDate(DateUtils.getNowDate());
        return limsChangeControlMapper.updateLimsChangeControl(c);
    }
}
