package com.wei.hrm.md.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.wei.hrm.md.mapper.HrmRankMapper;
import com.wei.hrm.md.domain.HrmRank;
import com.wei.hrm.md.service.IHrmRankService;

/**
 * 职级管理Service业务层处理
 *
 * @author wei
 * @date 2024-07-23
 */
@Service
public class HrmRankServiceImpl  extends ServiceImpl<HrmRankMapper, HrmRank>  implements IHrmRankService
{
    @Autowired
    private HrmRankMapper hrmRankMapper;

}
