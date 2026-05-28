package com.wei.mes.md.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wei.common.core.domain.TreeSelect;
import com.wei.common.core.domain.entity.MdItemType;

import java.util.List;

public interface IItemTypeService extends IService<MdItemType> {

    public List<MdItemType> selectItemTypeList(MdItemType itemType);

    public MdItemType selectItemTypeById(Long itemTypeId);

    public List<TreeSelect> buildTreeSelect(List<MdItemType> list);

    public String checkItemTypeCodeUnique(MdItemType itemType);

    public String checkItemTypeNameUnique(MdItemType itemType);

    public Integer insertItemType(MdItemType itemType);

    public Integer updateItemType(MdItemType itemType);

    public Integer removeItemType(Long itemTypeId);

    public boolean checkHasChild(Long itemTypeId);

    public boolean checkHasItem(Long itemTypeId);

    /**
     * 查询所有子类
     */
    List<MdItemType> selectChildrenByAncestor(Long ancestorId);
}
