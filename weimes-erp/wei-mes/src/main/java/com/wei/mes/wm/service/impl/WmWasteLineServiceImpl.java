package com.wei.mes.wm.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.wei.mes.wm.mapper.WmWasteLineMapper;
import com.wei.mes.wm.domain.WmWasteLine;
import com.wei.mes.wm.service.IWmWasteLineService;

/**
 * 生产废料单行Service业务层处理
 *
 * @author wei
 * @date 2024-05-11
 */
@Service
public class WmWasteLineServiceImpl  extends ServiceImpl<WmWasteLineMapper, WmWasteLine>  implements IWmWasteLineService
{
    @Autowired
    private WmWasteLineMapper wmWasteLineMapper;

    /**
     * 批量删除废料行数据
     * @param wasteIds
     * @return
     */
    @Override
    public int delWmWasteLineIds(List<Long> wasteIds) {
        return wmWasteLineMapper.delLineByWasteIds(wasteIds);
    }

    /**
     * 条件查询 废料行信息
     * @param wasteId 废料头部id
     * @return
     */
    @Override
    public List<WmWasteLine> selectWmWasteLineList(Long wasteId) {
        return this.lambdaQuery().eq(WmWasteLine::getWasteId, wasteId).list();
    }
}
