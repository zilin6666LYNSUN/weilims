package com.wei.mes.sales.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wei.common.core.domain.AjaxResult;
import com.wei.mes.sales.domain.TranOrderLine;

/**
 * 销售送货单列Service接口
 *
 * @author 堇年
 * @date 2024-09-13
 */
public interface ITranOrderLineService extends IService<TranOrderLine> {

    public AjaxResult execute(TranOrderLine tranOrderLine) throws Exception;
}
