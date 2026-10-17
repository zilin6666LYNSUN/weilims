package com.wei.lims.sample.service.impl;

import java.util.List;
import com.wei.lims.sample.domain.LimsSampleTestItem;
import com.wei.lims.sample.mapper.LimsSampleTestItemMapper;
import com.wei.lims.sample.service.ILimsSampleTestItemService;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsSampleTestItemServiceImpl implements ILimsSampleTestItemService {

    @Autowired
    private LimsSampleTestItemMapper limsSampleTestItemMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public List<LimsSampleTestItem> selectBySampleId(Long sampleId) {
        return limsSampleTestItemMapper.selectBySampleId(sampleId);
    }

    @Override
    public List<LimsSampleTestItem> selectLimsSampleTestItemList(LimsSampleTestItem item) {
        return limsSampleTestItemMapper.selectLimsSampleTestItemList(item);
    }

    @Override
    @Transactional
    public int insertLimsSampleTestItem(LimsSampleTestItem item) {
        int rows = limsSampleTestItemMapper.insertLimsSampleTestItem(item);
        limsAuditTrailService.recordFieldChanges("lims_sample_test_item", item.getId(),
                "INSERT", null, item, "新建样品检验项目");
        return rows;
    }

    @Override
    @Transactional
    public int batchInsert(List<LimsSampleTestItem> list) {
        int rows = limsSampleTestItemMapper.batchInsert(list);
        for (LimsSampleTestItem item : list) {
            limsAuditTrailService.recordFieldChanges("lims_sample_test_item", item.getId(),
                    "INSERT", null, item, "批量新建样品检验项目");
        }
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsSampleTestItem(LimsSampleTestItem item) {
        LimsSampleTestItem oldItem = limsSampleTestItemMapper.selectById(item.getId());
        int rows = limsSampleTestItemMapper.updateLimsSampleTestItem(item);
        limsAuditTrailService.recordFieldChanges("lims_sample_test_item", item.getId(),
                "UPDATE", oldItem, item, "更新样品检验项目");
        return rows;
    }

    @Override
    @Transactional
    public int deleteBySampleId(Long sampleId) {
        List<LimsSampleTestItem> oldItems = limsSampleTestItemMapper.selectBySampleId(sampleId);
        for (LimsSampleTestItem item : oldItems) {
            limsAuditTrailService.recordFieldChanges("lims_sample_test_item", item.getId(),
                    "DELETE", item, null, "删除样品全部检验项目");
        }
        return limsSampleTestItemMapper.deleteBySampleId(sampleId);
    }

    @Override
    @Transactional
    public int deleteById(Long id) {
        LimsSampleTestItem oldItem = limsSampleTestItemMapper.selectById(id);
        limsAuditTrailService.recordFieldChanges("lims_sample_test_item", id,
                "DELETE", oldItem, null, "删除样品检验项目");
        return limsSampleTestItemMapper.deleteById(id);
    }
}
