package com.wei.lims.entrust.mapper;

import java.util.List;
import com.wei.lims.entrust.domain.LimsEntrustItem;

public interface LimsEntrustItemMapper {
    public LimsEntrustItem selectLimsEntrustItemByItemId(Long itemId);
    public List<LimsEntrustItem> selectLimsEntrustItemList(LimsEntrustItem LimsEntrustItem);
    public int insertLimsEntrustItem(LimsEntrustItem limsentrustitem);
    public int updateLimsEntrustItem(LimsEntrustItem limsentrustitem);
    public int deleteLimsEntrustItemByItemId(Long[] itemIds);
}