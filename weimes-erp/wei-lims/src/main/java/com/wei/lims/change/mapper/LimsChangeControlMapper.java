package com.wei.lims.change.mapper;

import java.util.List;
import com.wei.lims.change.domain.LimsChangeControl;

public interface LimsChangeControlMapper {
    public LimsChangeControl selectByChangeId(Long changeId);
    public List<LimsChangeControl> selectLimsChangeControlList(LimsChangeControl changeControl);
    public int insertLimsChangeControl(LimsChangeControl changeControl);
    public int updateLimsChangeControl(LimsChangeControl changeControl);
    public int deleteLimsChangeControlByIds(Long[] changeIds);
}
