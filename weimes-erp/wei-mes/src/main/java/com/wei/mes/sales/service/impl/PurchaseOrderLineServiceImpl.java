package com.wei.mes.sales.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wei.mes.sales.domain.PurchaseOrderLine;
import com.wei.mes.sales.mapper.PurchaseOrderLineMapper;
import com.wei.mes.sales.service.IPurchaseOrderLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 采购订单列表Service业务层处理
 *
 * @author wei
 * @date 2024-11-02
 */
@Service
public class PurchaseOrderLineServiceImpl  extends ServiceImpl<PurchaseOrderLineMapper, PurchaseOrderLine>  implements IPurchaseOrderLineService
{
    @Autowired
    private PurchaseOrderLineMapper purchaseOrderLineMapper;

}
