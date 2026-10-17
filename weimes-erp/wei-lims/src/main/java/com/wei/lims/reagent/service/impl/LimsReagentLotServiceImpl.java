package com.wei.lims.reagent.service.impl;

import java.util.List;
import com.wei.lims.reagent.domain.LimsReagentLot;
import com.wei.lims.reagent.mapper.LimsReagentLotMapper;
import com.wei.lims.reagent.service.ILimsReagentLotService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsReagentLotServiceImpl implements ILimsReagentLotService {

    @Autowired
    private LimsReagentLotMapper limsReagentLotMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsReagentLot selectByLotId(Long lotId) {
        return limsReagentLotMapper.selectByLotId(lotId);
    }

    @Override
    public List<LimsReagentLot> selectByReagentId(Long reagentId) {
        return limsReagentLotMapper.selectByReagentId(reagentId);
    }

    @Override
    public List<LimsReagentLot> selectLimsReagentLotList(LimsReagentLot lot) {
        return limsReagentLotMapper.selectLimsReagentLotList(lot);
    }

    @Override
    @Transactional
    public int insertLimsReagentLot(LimsReagentLot lot) {
        int rows = limsReagentLotMapper.insertLimsReagentLot(lot);
        limsAuditTrailService.recordFieldChanges("lims_reagent_lot", lot.getLotId(),
                "INSERT", null, lot, "新建试剂批次");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsReagentLot(LimsReagentLot lot) {
        LimsReagentLot oldLot = limsReagentLotMapper.selectByLotId(lot.getLotId());
        int rows = limsReagentLotMapper.updateLimsReagentLot(lot);
        limsAuditTrailService.recordFieldChanges("lims_reagent_lot", lot.getLotId(),
                "UPDATE", oldLot, lot, "更新试剂批次");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsReagentLotByIds(Long[] lotIds) {
        for (Long lotId : lotIds) {
            LimsReagentLot oldLot = limsReagentLotMapper.selectByLotId(lotId);
            limsAuditTrailService.recordFieldChanges("lims_reagent_lot", lotId,
                    "DELETE", oldLot, null, "删除试剂批次");
        }
        return limsReagentLotMapper.deleteLimsReagentLotByIds(lotIds);
    }
}
