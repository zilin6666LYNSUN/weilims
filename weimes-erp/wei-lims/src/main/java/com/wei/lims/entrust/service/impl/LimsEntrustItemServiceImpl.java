package com.wei.lims.entrust.service.impl;

import java.util.List;
import com.wei.lims.entrust.domain.LimsEntrustItem;
import com.wei.lims.entrust.mapper.LimsEntrustItemMapper;
import com.wei.lims.entrust.service.ILimsEntrustItemService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsEntrustItemServiceImpl implements ILimsEntrustItemService {

    @Autowired
    private LimsEntrustItemMapper limsentrustitemMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsEntrustItem selectLimsEntrustItemByItemId(Long itemId) {
        return limsentrustitemMapper.selectLimsEntrustItemByItemId(itemId);
    }

    @Override
    public List<LimsEntrustItem> selectLimsEntrustItemList(LimsEntrustItem limsentrustitem) {
        return limsentrustitemMapper.selectLimsEntrustItemList(limsentrustitem);
    }

    @Override
    @Transactional
    public int insertLimsEntrustItem(LimsEntrustItem limsentrustitem) {
        int rows = limsentrustitemMapper.insertLimsEntrustItem(limsentrustitem);
        limsAuditTrailService.recordFieldChanges("lims_entrust_item", limsentrustitem.getItemId(), "INSERT", null, limsentrustitem, "新建委托检验项目");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsEntrustItem(LimsEntrustItem limsentrustitem) {
        LimsEntrustItem old = limsentrustitemMapper.selectLimsEntrustItemByItemId(limsentrustitem.getItemId());
        int rows = limsentrustitemMapper.updateLimsEntrustItem(limsentrustitem);
        limsAuditTrailService.recordFieldChanges("lims_entrust_item", limsentrustitem.getItemId(), "UPDATE", old, limsentrustitem, "更新委托检验项目");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsEntrustItemByItemId(Long[] itemIds) {
        for (Long itemId : itemIds) {
            LimsEntrustItem old = limsentrustitemMapper.selectLimsEntrustItemByItemId(itemId);
            limsAuditTrailService.recordFieldChanges("lims_entrust_item", itemId, "DELETE", old, null, "删除委托检验项目");
        }
        return limsentrustitemMapper.deleteLimsEntrustItemByItemId(itemIds);
    }
}