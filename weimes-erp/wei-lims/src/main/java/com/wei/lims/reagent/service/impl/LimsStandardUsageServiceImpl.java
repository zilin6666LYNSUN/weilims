package com.wei.lims.reagent.service.impl;

import java.math.BigDecimal;
import java.util.List;
import com.wei.lims.reagent.domain.LimsReferenceStandard;
import com.wei.lims.reagent.domain.LimsStandardUsage;
import com.wei.lims.reagent.mapper.LimsReferenceStandardMapper;
import com.wei.lims.reagent.mapper.LimsStandardUsageMapper;
import com.wei.lims.reagent.service.ILimsStandardUsageService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsStandardUsageServiceImpl implements ILimsStandardUsageService {

    @Autowired
    private LimsStandardUsageMapper limsStandardUsageMapper;

    @Autowired
    private LimsReferenceStandardMapper limsReferenceStandardMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsStandardUsage selectByUsageId(Long usageId) {
        return limsStandardUsageMapper.selectByUsageId(usageId);
    }

    @Override
    public List<LimsStandardUsage> selectByStandardId(Long standardId) {
        return limsStandardUsageMapper.selectByStandardId(standardId);
    }

    @Override
    public List<LimsStandardUsage> selectLimsStandardUsageList(LimsStandardUsage usage) {
        return limsStandardUsageMapper.selectLimsStandardUsageList(usage);
    }

    @Override
    @Transactional
    public int insertLimsStandardUsage(LimsStandardUsage usage) {
        // 联动扣减标准品剩余量
        LimsReferenceStandard standard = limsReferenceStandardMapper.selectByStandardId(usage.getStandardId());
        if (standard != null && usage.getQuantityUsed() != null) {
            BigDecimal oldRemaining = standard.getRemainingQuantity() == null ? BigDecimal.ZERO : standard.getRemainingQuantity();
            BigDecimal newRemaining = oldRemaining.subtract(usage.getQuantityUsed());
            if (newRemaining.compareTo(BigDecimal.ZERO) < 0) {
                newRemaining = BigDecimal.ZERO;
            }
            usage.setRemainingAfter(newRemaining);
            LimsReferenceStandard upd = new LimsReferenceStandard();
            upd.setStandardId(standard.getStandardId());
            upd.setRemainingQuantity(newRemaining);
            limsReferenceStandardMapper.updateLimsReferenceStandard(upd);
            limsAuditTrailService.recordFieldChanges("lims_reference_standard", standard.getStandardId(),
                    "UPDATE", standard, upd, "标准品使用扣减余量");
        }
        int rows = limsStandardUsageMapper.insertLimsStandardUsage(usage);
        limsAuditTrailService.recordFieldChanges("lims_standard_usage", usage.getUsageId(),
                "INSERT", null, usage, "新建标准品使用记录");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsStandardUsage(LimsStandardUsage usage) {
        LimsStandardUsage oldUsage = limsStandardUsageMapper.selectByUsageId(usage.getUsageId());
        int rows = limsStandardUsageMapper.updateLimsStandardUsage(usage);
        limsAuditTrailService.recordFieldChanges("lims_standard_usage", usage.getUsageId(),
                "UPDATE", oldUsage, usage, "更新标准品使用记录");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsStandardUsageByIds(Long[] usageIds) {
        for (Long usageId : usageIds) {
            LimsStandardUsage oldUsage = limsStandardUsageMapper.selectByUsageId(usageId);
            limsAuditTrailService.recordFieldChanges("lims_standard_usage", usageId,
                    "DELETE", oldUsage, null, "删除标准品使用记录");
        }
        return limsStandardUsageMapper.deleteLimsStandardUsageByIds(usageIds);
    }
}
