package com.wei.lims.test.service.impl;

import java.util.List;
import com.wei.common.utils.DateUtils;
import com.wei.lims.esign.service.ILimsAuditTrailService;
import com.wei.lims.test.domain.LimsTestOrder;
import com.wei.lims.test.mapper.LimsTestOrderMapper;
import com.wei.lims.test.service.ILimsTestOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimsTestOrderServiceImpl implements ILimsTestOrderService {

    @Autowired
    private LimsTestOrderMapper limsTestOrderMapper;

    @Autowired
    private ILimsAuditTrailService limsAuditTrailService;

    @Override
    public LimsTestOrder selectLimsTestOrderByTestOrderId(Long testOrderId) {
        return limsTestOrderMapper.selectLimsTestOrderByTestOrderId(testOrderId);
    }

    @Override
    public List<LimsTestOrder> selectLimsTestOrderList(LimsTestOrder limsTestOrder) {
        return limsTestOrderMapper.selectLimsTestOrderList(limsTestOrder);
    }

    @Override
    @Transactional
    public int insertLimsTestOrder(LimsTestOrder limsTestOrder) {
        limsTestOrder.setCreateTime(DateUtils.getNowDate());
        int rows = limsTestOrderMapper.insertLimsTestOrder(limsTestOrder);
        limsAuditTrailService.recordFieldChanges("lims_test_order", limsTestOrder.getTestOrderId(),
                "INSERT", null, limsTestOrder, "新建检验订单");
        return rows;
    }

    @Override
    @Transactional
    public int updateLimsTestOrder(LimsTestOrder limsTestOrder) {
        LimsTestOrder old = limsTestOrderMapper.selectLimsTestOrderByTestOrderId(limsTestOrder.getTestOrderId());
        limsTestOrder.setUpdateTime(DateUtils.getNowDate());
        int rows = limsTestOrderMapper.updateLimsTestOrder(limsTestOrder);
        limsAuditTrailService.recordFieldChanges("lims_test_order", limsTestOrder.getTestOrderId(),
                "UPDATE", old, limsTestOrder, "更新检验订单");
        return rows;
    }

    @Override
    @Transactional
    public int deleteLimsTestOrderByTestOrderIds(Long[] testOrderIds) {
        for (Long id : testOrderIds) {
            limsAuditTrailService.recordFieldChanges("lims_test_order", id,
                    "DELETE", null, null, "删除检验订单");
        }
        return limsTestOrderMapper.deleteLimsTestOrderByTestOrderIds(testOrderIds);
    }

    @Override
    public String checkTestOrderCodeUnique(LimsTestOrder limsTestOrder) {
        LimsTestOrder existing = limsTestOrderMapper.selectLimsTestOrderByTestOrderCode(limsTestOrder.getTestOrderCode());
        if (existing != null && !existing.getTestOrderId().equals(limsTestOrder.getTestOrderId())) {
            return "1"; // NOT_UNIQUE
        }
        return "0"; // UNIQUE
    }

    @Override
    @Transactional
    public int submitTestOrder(Long testOrderId) {
        LimsTestOrder order = limsTestOrderMapper.selectLimsTestOrderByTestOrderId(testOrderId);
        if (order == null) return 0;
        order.setStatus("SUBMITTED");
        int rows = limsTestOrderMapper.updateLimsTestOrder(order);
        limsAuditTrailService.recordFieldChanges("lims_test_order", testOrderId,
                "UPDATE", null, order, "提交检验订单");
        return rows;
    }

    @Override
    @Transactional
    public int completeTestResult(Long testOrderId) {
        LimsTestOrder order = limsTestOrderMapper.selectLimsTestOrderByTestOrderId(testOrderId);
        if (order == null) return 0;
        order.setStatus("TESTED");
        int rows = limsTestOrderMapper.updateLimsTestOrder(order);
        limsAuditTrailService.recordFieldChanges("lims_test_order", testOrderId,
                "UPDATE", null, order, "检验结果录入完成");
        return rows;
    }

    @Override
    @Transactional
    public int reviewTestOrder(Long testOrderId, String reviewResult, String comment) {
        LimsTestOrder order = limsTestOrderMapper.selectLimsTestOrderByTestOrderId(testOrderId);
        if (order == null) return 0;
        if ("PASS".equals(reviewResult)) {
            order.setStatus("REVIEWED");
        } else {
            order.setStatus("TESTING");
        }
        int rows = limsTestOrderMapper.updateLimsTestOrder(order);
        limsAuditTrailService.recordFieldChanges("lims_test_order", testOrderId,
                "UPDATE", null, order, "检验复核: " + reviewResult + (comment != null ? " - " + comment : ""));
        return rows;
    }

    @Override
    @Transactional
    public int approveTestOrder(Long testOrderId, String approveResult, String comment) {
        LimsTestOrder order = limsTestOrderMapper.selectLimsTestOrderByTestOrderId(testOrderId);
        if (order == null) return 0;
        if ("PASS".equals(approveResult)) {
            order.setStatus("APPROVED");
        } else {
            order.setStatus("REVIEWING");
        }
        int rows = limsTestOrderMapper.updateLimsTestOrder(order);
        limsAuditTrailService.recordFieldChanges("lims_test_order", testOrderId,
                "UPDATE", null, order, "检验批准: " + approveResult + (comment != null ? " - " + comment : ""));
        return rows;
    }
}
