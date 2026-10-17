package com.wei.lims.reagent.service.impl;

import java.util.Date;
import java.util.List;
import com.wei.common.utils.DateUtils;
import com.wei.lims.reagent.domain.LimsCultureCollection;
import com.wei.lims.reagent.mapper.LimsCultureCollectionMapper;
import com.wei.lims.reagent.service.ILimsCultureCollectionService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsCultureCollectionServiceImpl implements ILimsCultureCollectionService {

    @Autowired
    private LimsCultureCollectionMapper limsCultureCollectionMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsCultureCollection selectByCultureId(Long cultureId) {
        return limsCultureCollectionMapper.selectByCultureId(cultureId);
    }

    @Override
    public List<LimsCultureCollection> selectLimsCultureCollectionList(LimsCultureCollection culture) {
        return limsCultureCollectionMapper.selectLimsCultureCollectionList(culture);
    }

    @Override
    @Transactional
    public int insertLimsCultureCollection(LimsCultureCollection culture) {
        int rows = limsCultureCollectionMapper.insertLimsCultureCollection(culture);
        limsAuditTrailService.recordFieldChanges("lims_culture_collection", culture.getCultureId(),
                "INSERT", null, culture, "新建菌种保藏记录");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsCultureCollection(LimsCultureCollection culture) {
        LimsCultureCollection oldCulture = limsCultureCollectionMapper.selectByCultureId(culture.getCultureId());
        int rows = limsCultureCollectionMapper.updateLimsCultureCollection(culture);
        limsAuditTrailService.recordFieldChanges("lims_culture_collection", culture.getCultureId(),
                "UPDATE", oldCulture, culture, "更新菌种保藏记录");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsCultureCollectionByIds(Long[] cultureIds) {
        for (Long cultureId : cultureIds) {
            LimsCultureCollection oldCulture = limsCultureCollectionMapper.selectByCultureId(cultureId);
            limsAuditTrailService.recordFieldChanges("lims_culture_collection", cultureId,
                    "DELETE", oldCulture, null, "删除菌种保藏记录");
        }
        return limsCultureCollectionMapper.deleteLimsCultureCollectionByIds(cultureIds);
    }

    @Override
    @Transactional
    public int receiveCulture(Long cultureId) {
        LimsCultureCollection oldCulture = limsCultureCollectionMapper.selectByCultureId(cultureId);
        LimsCultureCollection upd = new LimsCultureCollection();
        upd.setCultureId(cultureId);
        upd.setStatus("ACTIVE");
        upd.setReceivedDate(DateUtils.getNowDate());
        int rows = limsCultureCollectionMapper.updateLimsCultureCollection(upd);
        limsAuditTrailService.recordFieldChanges("lims_culture_collection", cultureId,
                "UPDATE", oldCulture, upd, "菌种接收入库");
        return rows;
    }

    @Override
    @Transactional
    public int passageCulture(Long cultureId, String viabilityResult) {
        LimsCultureCollection oldCulture = limsCultureCollectionMapper.selectByCultureId(cultureId);
        int newCount = oldCulture.getPassageCount() == null ? 1 : oldCulture.getPassageCount() + 1;
        LimsCultureCollection upd = new LimsCultureCollection();
        upd.setCultureId(cultureId);
        upd.setPassageCount(newCount);
        upd.setViabilityResult(viabilityResult);
        upd.setViabilityTestDate(new Date());
        int rows = limsCultureCollectionMapper.updateLimsCultureCollection(upd);
        limsAuditTrailService.recordFieldChanges("lims_culture_collection", cultureId,
                "UPDATE", oldCulture, upd, "菌种传代");
        return rows;
    }

    @Override
    @Transactional
    public int discardCulture(Long cultureId, String reason) {
        LimsCultureCollection oldCulture = limsCultureCollectionMapper.selectByCultureId(cultureId);
        LimsCultureCollection upd = new LimsCultureCollection();
        upd.setCultureId(cultureId);
        upd.setStatus("DISCARDED");
        int rows = limsCultureCollectionMapper.updateLimsCultureCollection(upd);
        limsAuditTrailService.recordFieldChanges("lims_culture_collection", cultureId,
                "UPDATE", oldCulture, upd, "菌种销毁出库：" + (reason == null ? "" : reason));
        return rows;
    }
}
