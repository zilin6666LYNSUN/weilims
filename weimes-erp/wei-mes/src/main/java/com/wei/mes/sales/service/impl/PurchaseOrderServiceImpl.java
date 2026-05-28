package com.wei.mes.sales.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wei.mes.sales.domain.PurchaseOrder;
import com.wei.mes.sales.mapper.PurchaseOrderMapper;
import com.wei.mes.sales.service.IPurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 采购订单Service业务层处理
 *
 * @author wei
 * @date 2024-11-02
 */
@Service
public class PurchaseOrderServiceImpl  extends ServiceImpl<PurchaseOrderMapper, PurchaseOrder>  implements IPurchaseOrderService
{
    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;

}
