package com.wei.lims.reagent.service.impl;

import java.util.List;
import com.wei.lims.reagent.domain.LimsReferenceStandard;
import com.wei.lims.reagent.mapper.LimsReferenceStandardMapper;
import com.wei.lims.reagent.service.ILimsReferenceStandardService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsReferenceStandardServiceImpl implements ILimsReferenceStandardService {

    @Autowired
    private LimsReferenceStandardMapper limsReferenceStandardMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsReferenceStandard selectByStandardId(Long standardId) {
        return limsReferenceStandardMapper.selectByStandardId(standardId);
    }

    @Override
    public List<LimsReferenceStandard> selectLimsReferenceStandardList(LimsReferenceStandard standard) {
        return limsReferenceStandardMapper.selectLimsReferenceStandardList(standard);
    }

    @Override
    @Transactional
    public int insertLimsReferenceStandard(LimsReferenceStandard standard) {
        int rows = limsReferenceStandardMapper.insertLimsReferenceStandard(standard);
        limsAuditTrailService.recordFieldChanges("lims_reference_standard", standard.getStandardId(),
                "INSERT", null, standard, "新建标准品/参考标准");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsReferenceStandard(LimsReferenceStandard standard) {
        LimsReferenceStandard oldStandard = limsReferenceStandardMapper.selectByStandardId(standard.getStandardId());
        int rows = limsReferenceStandardMapper.updateLimsReferenceStandard(standard);
        limsAuditTrailService.recordFieldChanges("lims_reference_standard", standard.getStandardId(),
                "UPDATE", oldStandard, standard, "更新标准品/参考标准");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsReferenceStandardByIds(Long[] standardIds) {
        for (Long standardId : standardIds) {
            LimsReferenceStandard oldStandard = limsReferenceStandardMapper.selectByStandardId(standardId);
            limsAuditTrailService.recordFieldChanges("lims_reference_standard", standardId,
                    "DELETE", oldStandard, null, "删除标准品/参考标准");
        }
        return limsReferenceStandardMapper.deleteLimsReferenceStandardByIds(standardIds);
    }
}
