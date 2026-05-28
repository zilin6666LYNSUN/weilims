package com.wei.lims.test.mapper;

import java.util.List;
import com.wei.lims.test.domain.LimsTestOrder;

/**
 * 检验订单Mapper接口
 */
public interface LimsTestOrderMapper {

    public LimsTestOrder selectLimsTestOrderByTestOrderId(Long testOrderId);

    public LimsTestOrder selectLimsTestOrderByTestOrderCode(String testOrderCode);

    public List<LimsTestOrder> selectLimsTestOrderList(LimsTestOrder limsTestOrder);

    public int insertLimsTestOrder(LimsTestOrder limsTestOrder);

    public int updateLimsTestOrder(LimsTestOrder limsTestOrder);

    public int deleteLimsTestOrderByTestOrderIds(Long[] testOrderIds);

    public int deleteLimsTestOrderByTestOrderId(Long testOrderId);

    public String checkTestOrderCodeUnique(LimsTestOrder limsTestOrder);
}
