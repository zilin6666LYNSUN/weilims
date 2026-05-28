package com.wei.mes.pro.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wei.mes.pro.domain.ProClientOrder;
import com.wei.mes.pro.domain.ProWorkorder;

/**
 * 客户订单Service接口
 *
 * @author wei
 * @date 2024-05-01
 */
public interface IProClientOrderService extends IService<ProClientOrder> {

    /**
     * 生成生产订单
     */
    ProWorkorder generateWorkOrder(ProClientOrder clientOrder);

    void saveClientOrder(ProClientOrder proClientOrder);
}
