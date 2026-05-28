package com.wei.lims.test.service;

import java.util.List;
import com.wei.lims.test.domain.LimsTestOrder;

/**
 * 检验订单Service接口
 */
public interface ILimsTestOrderService {

    public LimsTestOrder selectLimsTestOrderByTestOrderId(Long testOrderId);

    public List<LimsTestOrder> selectLimsTestOrderList(LimsTestOrder limsTestOrder);

    public int insertLimsTestOrder(LimsTestOrder limsTestOrder);

    public int updateLimsTestOrder(LimsTestOrder limsTestOrder);

    public int deleteLimsTestOrderByTestOrderIds(Long[] testOrderIds);

    public String checkTestOrderCodeUnique(LimsTestOrder limsTestOrder);

    /**
     * 提交检验订单(启动工作流)
     */
    public int submitTestOrder(Long testOrderId);

    /**
     * 检验结果录入完成
     */
    public int completeTestResult(Long testOrderId);

    /**
     * 检验复核
     */
    public int reviewTestOrder(Long testOrderId, String reviewResult, String comment);

    /**
     * 检验批准
     */
    public int approveTestOrder(Long testOrderId, String approveResult, String comment);
}
