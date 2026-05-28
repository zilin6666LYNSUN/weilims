package com.wei.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wei.system.domain.SysConfigJasypt;
import com.wei.system.mapper.SysConfigJasyptMapper;
import com.wei.system.service.ISysConfigJasyptService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 加密配置Service业务层处理
 *
 * @author wei
 * @date 2024-07-01
 */
@Service
public class SysConfigJasyptServiceImpl extends ServiceImpl<SysConfigJasyptMapper, SysConfigJasypt> implements ISysConfigJasyptService {
    @Resource
    private SysConfigJasyptMapper sysConfigJasyptMapper;

}
