package com.wei.lims.reagent.service.impl;

import java.util.List;
import com.wei.lims.reagent.domain.LimsReferenceStandard;
import com.wei.lims.reagent.mapper.LimsReferenceStandardMapper;
import com.wei.lims.reagent.service.ILimsReferenceStandardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LimsReferenceStandardServiceImpl implements ILimsReferenceStandardService {

    @Autowired
    private LimsReferenceStandardMapper limsReferenceStandardMapper;

    @Override
    public LimsReferenceStandard selectByStandardId(Long standardId) {
        return limsReferenceStandardMapper.selectByStandardId(standardId);
    }

    @Override
    public List<LimsReferenceStandard> selectLimsReferenceStandardList(LimsReferenceStandard standard) {
        return limsReferenceStandardMapper.selectLimsReferenceStandardList(standard);
    }

    @Override
    public int insertLimsReferenceStandard(LimsReferenceStandard standard) {
        return limsReferenceStandardMapper.insertLimsReferenceStandard(standard);
    }

    @Override
    public int updateLimsReferenceStandard(LimsReferenceStandard standard) {
        return limsReferenceStandardMapper.updateLimsReferenceStandard(standard);
    }

    @Override
    public int deleteLimsReferenceStandardByIds(Long[] standardIds) {
        return limsReferenceStandardMapper.deleteLimsReferenceStandardByIds(standardIds);
    }
}
