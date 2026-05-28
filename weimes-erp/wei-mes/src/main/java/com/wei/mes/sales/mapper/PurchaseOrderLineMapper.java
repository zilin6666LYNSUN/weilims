package com.wei.mes.sales.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wei.mes.sales.domain.PurchaseOrderLine;
import org.apache.ibatis.annotations.Mapper;

/**
 * 采购订单列表Mapper接口
 *
 * @author wei
 * @date 2024-11-02
 */
@Mapper
public interface PurchaseOrderLineMapper extends BaseMapper<PurchaseOrderLine> {

}
