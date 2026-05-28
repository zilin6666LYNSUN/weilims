package com.wei.mes.common.service.impl;

import com.wei.mes.common.dto.SelectInfoDTO;
import com.wei.mes.common.mapper.CommonMapper;
import com.wei.mes.common.service.ICommonService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 共通service
 *
 * @author wei
 * @date 2024/8/23 18:37
 */
@Service
public class CommonServiceImpl implements ICommonService {


    @Resource
    private CommonMapper commonMapper;

    /**
     * 查询仓库不包含参数列表的仓库信息
     *
     * @param notInCodeList
     */
    @Override
    public List<SelectInfoDTO> selectAllWarehouse(List<String> notInCodeList) {
        return this.commonMapper.selectAllWarehouse(notInCodeList);
    }

    /**
     * 查询库位不包含参数列表的仓库信息
     *
     * @param notInCodeList
     */
    @Override
    public List<SelectInfoDTO> selectAllArea(List<String> notInCodeList) {
        return this.commonMapper.selectAllArea(notInCodeList);
    }

    /**
     * 查询库区不包含参数列表的仓库信息
     *
     * @param notInCodeList
     */
    @Override
    public List<SelectInfoDTO> selectAllLocation(List<String> notInCodeList) {
        return this.commonMapper.selectAllLocation(notInCodeList);
    }
}
